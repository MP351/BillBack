package api

import InvalidCredentialsException
import LoginEntitySber
import UsersEntity
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.SerializationFeature
import db.DbConnection
import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.auth.principal
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondBytes
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.webModule() {
    val simpleJwt = SimpleJWT("my-super-secret-for-jwt")
    install(Authentication) {
        jwt {
            verifier(simpleJwt.verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("login").asString())
            }
        }
    }
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    val dbEntity = DbConnection.getInstance()

    routing {
        route("/login") {
            post {
                val post = call.receive<LoginEntitySber>()
                dbEntity.getLoginsMap()[post.login]?.apply {
                    if (password != post.password) throw InvalidCredentialsException("Invalid credentials")
                    call.respond(mapOf("token" to simpleJwt.sign(login)))
                }
            }
        }

        route("/admin") {
//            intercept(ApplicationCallPipeline.Features) {
//                if (call.principal<UserIdPrincipal>()?.name != "admin")
//                    call.respond("Not admin")
//            }
//            get("/test") {
////                call.respond(mapOf("logins" to dbEntity.getLogins()))
//                call.respond(dbEntity.getLogins())
//            }

            authenticate {
                get("logins") {
                    call.respond(dbEntity.getLogins())
                }

                post("login") {
                    dbEntity.insertLogin(call.receive())
                    call.respond(mapOf("OK" to true))
                }

                get("tariffs") {
                    call.respond(dbEntity.getTariffs())
                }

                post("tariff") {
                    dbEntity.insertTariff(call.receive())
                    call.respond(mapOf("OK" to true))
                }

                get("users") {
                    call.respond(dbEntity.getUsers())
                }

                post("users") {
                    dbEntity.insertUser(call.receive())
                    call.respond(mapOf("OK" to true))
                }
            }
        }
    }
}

open class SimpleJWT(val secret: String) {
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm).build()
    fun sign(name: String): String = JWT.create().withClaim("login", name).sign(algorithm)
}
