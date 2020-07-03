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
                when(val login = LoginProcessor.getLoginByName(post.login)) {
                    is Response.Success<LoginEntityDB> -> {
                        val qLogin = login.data["login"]
                        if (qLogin?.password == post.password) {
                            call.respond(mapOf("token" to simpleJwt.sign(post.login)))
                        } else{
                            call.respond(HttpStatusCode.Unauthorized)
                        }
                    }
                    is Response.Failure -> {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
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
                    when (val response = LoginProcessor.getLoginsNoPassword()) {
                        is Response.Success<*> -> {
                            call.respond(response.code, response.data)
                        }
                        is Response.Failure -> {
                            call.respond(response.code)
                        }
                    }
                }

                post("login") {
                    when (val response = LoginProcessor.addLogin(call.receive())) {
                        is Response.Success<*> -> {
                            call.respond(response.code, response.data)
                        }
                        is Response.Failure -> {
                            call.respond(response.code)
                        }
                    }
                }

                get("tariffs") {
                    when (val response = TariffsProcessor.getTariffs()) {
                        is Response.Success<*> -> {
                            call.respond(response.code, response.data)
                        }
                        is Response.Failure -> {
                            call.respond(response.code, response.message)
                        }
                    }
                }

                post("tariff") {
                    when (val response = TariffsProcessor.addTariff(call.receive())) {
                        is Response.Success<*> -> {
                            call.respond(response.code, response.data)
                        }
                        is Response.Failure -> {
                            call.respond(response.code)
                        }
                    }
                }

                get("users") {
                    if (call.parameters["tariff"].equals("true"))
                        when(val response = UsersProcessor.getUsersWithTariffs()) {
                            is Response.Success<*> -> {
                                call.respond(response.code, response.data)
                            }
                            is Response.Failure -> {
                                call.respond(response.code, response.message)
                            }
                        }
                    else
                        when (val response = UsersProcessor.getUsers()) {
                            is Response.Success<*> -> {
                                call.respond(response.code, response.data)
                            }
                            is Response.Failure -> {
                                call.respond(response.code, response.message)
                            }
                        }
                }

                post("users") {
                    when (val response = UsersProcessor.addUser(call.receive())) {
                        is Response.Success<*> -> {
                            call.respond(response.code, response.data)
                        }
                        is Response.Failure -> {
                            call.respond(response.code)
                        }
                    }
                }

//                get("payments") {
//                    call.respond(dbEntity.getPayments())
//                }
            }
        }
    }
}

open class SimpleJWT(secret: String) {
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm).build()
    fun sign(name: String): String = JWT.create().withClaim("login", name).sign(algorithm)
}
