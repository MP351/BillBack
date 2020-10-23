package api

import LoginEntity
import LoginEntityDB
import LoginEntityNoPassword
import api.util.Response
import db.tables.ApiUser
import db.tables.ApiUsers
import db.tables.ApiUsersCRUD
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.log

object LoginProcessor {
    private val apiUsersCRUD = ApiUsersCRUD

    fun getLogins(): List<ApiUser> {
        return transaction {
            ApiUser.all().toList()
        }
    }

    fun getLoginsNoPassword(): Response<List<LoginEntityNoPassword>> {
        return try {
            val hm = HashMap<String, List<LoginEntityNoPassword>>().apply{
                put("login", apiUsersCRUD.getAll().map {
                    LoginEntityNoPassword(it.id.value, it.login)
                })
            }

            Response.Success(HttpStatusCode.OK, hm)
        } catch (t: Throwable) {
            Response.Failure(HttpStatusCode.BadRequest, t.message.toString())
        }
    }

    fun getLoginByName(name: String): Response<LoginEntityDB> {
        return try {
            val hm = HashMap<String, LoginEntityDB>()
            val login = apiUsersCRUD.getByName(name)
            hm["login"] = LoginEntityDB(login.id.value, login.login, login.password)
            Response.Success(HttpStatusCode.OK, hm)
        } catch (t: Throwable) {
            Response.Failure(HttpStatusCode.BadRequest, t.message.toString())
        }
    }

    fun addLogin(login: LoginEntity): Int? {
        return transaction {
            ApiUser.new {
                this.login = login.login
                this.password = login.password
            }.id.value
        }
    }

    fun getLoginByName(entity: LoginEntity): ApiUser? {
        return transaction {
            ApiUser.find {
                (ApiUsers.login eq entity.login) and (ApiUsers.password eq entity.password)
            }.firstOrNull()
        }
    }
}