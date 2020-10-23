package api

import LoginEntityDB
import LoginEntity
import api.util.Response
import billing.TariffsProcessor
import billing.UsersProcessor
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*

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

    routing {
        route("/login") {
            post {
                val post = call.receive<LoginEntity>()
                if (login(post)) {
                    call.respond(mapOf("token" to simpleJwt.sign(post.login)))
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
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
                    call.respond(getLogins())
                }

                get("tariffs") {
                    call.respond(getTariffs())
                }

                get("users") {
                    if (call.parameters["tariff"].equals("true"))
                        call.respond(getAllUsersWithTariffs())
                    else
                        call.respond(getAllUsers())
                }
            }
        }
    }
}

open class SimpleJWT(secret: String) {
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm).build()
    fun sign(name: String): String = JWT.create().withClaim("login", name).sign(algorithm)
}
