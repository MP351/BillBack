package db.tables

import LoginEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.IllegalArgumentException

object ApiUsers: IntIdTable() {
    val login: Column<String> = varchar("login", 50).uniqueIndex()
    val password: Column<String> = varchar("password", 50)
}

class ApiUser(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<ApiUser>(ApiUsers)
    var login by ApiUsers.login
    var password by ApiUsers.password

    override fun toString(): String {
        return "${id.value} $login"
    }
}

object ApiUsersCRUD {
    fun add(entity: LoginEntity): EntityID<Int> {
        return ApiUser.new {
                login = entity.login
                password = entity.password
        }.id
    }

    fun getAll(): List<ApiUser> {
        return ApiUser.all().toList()
    }

    fun getById(id: Int): ApiUser {
        return ApiUser.findById(id) ?: throw NoSuchElementException("No such ApiUser")
    }

    fun getByName(name: String): ApiUser {
        val user = ApiUser.find {
            ApiUsers.login eq name
        }.toList()

        when(user.size) {
            0 -> {
                return user.first()
            }
            1 -> {
                throw NoSuchElementException("No such user")
            }
            else -> {
                throw IllegalArgumentException("Too many users")
            }
        }
    }
}