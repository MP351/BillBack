
import api.webModule
import db.DbConnection
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.jwt
import io.ktor.features.ContentNegotiation
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.html.*
import org.apache.log4j.BasicConfigurator
import java.util.*

fun main() {
    BasicConfigurator.configure()
    Class.forName("org.sqlite.JDBC")
//
    watcher.Watcher("/home/maxpayne/Share/SberTest").isRunning = true
    DbConnection.getInstance().initDb()
    embeddedServer(Netty, port = 8080) {
        webModule()
    }.start(true)
}

fun Application.mymodule() {
    val simpleJWT = SimpleJWT("my-super-secret-for-jwt")
    install(Authentication) {
//        basic("test1") {
//            realm = "myrealm"
//            validate {
//                if (it.name == "user" && it.password == "password") UserIdPrincipal("user") else null
//            }
//        }
//        basic("test2") {
//            realm = "testrealm"
//            validate {
//                if (it.name == "test2" && it.password == "password2") UserIdPrincipal("test2") else error(Http2Error.INTERNAL_ERROR)
//            }
//        }
        jwt {
            verifier(simpleJWT.verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("name").asString())
            }
        }
    }
    install(ContentNegotiation) {
        jackson {

        }
    }

//    data class User(val name: String, val password: String)

    val users = Collections.synchronizedMap(
            listOf(User("test", "test"))
                    .associateBy { it.name }
                    .toMutableMap())
    routing {
//        authenticate("test1") {
//            get("/") {
//                val principal: UserIdPrincipal? = call.authentication.principal()
//                call.respondText {
//                    principal.toString()
//                }
//            }
//        }
//
//        authenticate("test2") {
//            route("test") {
//                get("/snippets/{login}") {
//                    call.respondText { call.parameters["login"] ?: "" }
//                }
//                get("/") {
//                    getServerInfo(call)
//                }
//                install(StatusPages) {
//                    exception<Throwable> { e ->
//                        call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.BadGateway)
//                    }
//                }
//            }
//        }

        post("/login-register") {
            val post = call.receive<LoginRegister>()
            val user = users.getOrPut(post.user) {
                User(post.user, post.password)
            }
            if (user.password != post.password) error("Invalid Credentials")
            call.respond(mapOf("token" to simpleJWT.sign(user.name)))
        }
    }
}

suspend fun getServerInfo(call: ApplicationCall) {
    call.respondHtml {
        head{
            title {
                +"KtorServer info"
            }
        }
        body {
            h1 {
                +"KtorServer"
            }
            p {
                +"This server provides OUIs (Organizationally Unique Identifiers) that have been assigned to a manufacturer by IEEE."
                +"You can lookup OUIs using the manufacturer name and also discover the manufacturer for a given OUI."
                +"See the GitHub site for more information: https://github.com/bwixted/ktorserver"
            }
        }
    }
}

suspend fun internalServerError(call: ApplicationCall) {
    call.respond(HttpStatusCode.InternalServerError, "error")
}

//open class SimpleJWT(val secret: String) {
//    private val algorithm = Algorithm.HMAC256(secret)
//    val verifier = JWT.require(algorithm).build()
//    fun sign(name: String): String = JWT.create().withClaim("name", name).sign(algorithm)
//}

//class Authentication(configuration: Configuration) {
//    val name = configuration.name
//    val password = configuration.password
//
//    class Configuration {
//        var name = "user" // Mutable property.
//        var password = "password"
//    }
//
//    fun basic() {
//
//    }
//    companion object Feature: ApplicationFeature<ApplicationCallPipeline, Authentication.Configuration, Authentication> {
//        override val key: AttributeKey<Authentication>
//            get() = AttributeKey("Authentication")
//
//        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): Authentication {
//            val configuration= Authentication.Configuration().apply(configure)
//
//            val feature = Authentication(configuration)
//
//            pipeline.intercept(ApplicationCallPipeline.Call) {
//
//            }
//            return feature
//        }
//
//        fun basic() {
//
//        }
//    }
//}

class LoginRegister(val user: String, val password: String)