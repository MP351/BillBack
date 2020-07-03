package api

import LoginEntity
import LoginEntityDB
import LoginEntityNoPassword
import api.util.Response
import db.tables.ApiUsersCRUD
import io.ktor.http.HttpStatusCode

object LoginProcessor {
    private val apiUsersCRUD = ApiUsersCRUD

    fun getLogins(): Response<List<LoginEntityDB>> {
        return try {
            val hm = HashMap<String, List<LoginEntityDB>>().apply{
                put("login", apiUsersCRUD.getAll().map {
                    LoginEntityDB(it.id.value, it.login, it.password)
                })
            }
            Response.Success(HttpStatusCode.OK, hm)
        } catch (t: Throwable) {
            Response.Failure(HttpStatusCode.BadRequest, t.message.toString())
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

    fun addLogin(login: LoginEntity): Response<Int> {
        return try {
            val hm = HashMap<String, Int>().apply {
                put("id", apiUsersCRUD.add(login).value)
            }
            Response.Success(HttpStatusCode.Created, hm)
        } catch (t: Throwable) {
            Response.Failure(HttpStatusCode.BadRequest, t.message.toString())
        }
    }
}