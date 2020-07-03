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

object ApiUsersCRUD: DbQueries<LoginEntity, ApiUser> {
    override fun add(entity: LoginEntity): EntityID<Int> {
        return transaction {
            ApiUser.new {
                login = entity.login
                password = entity.password
            }.id
        }
    }

    override fun getAll(): List<ApiUser> {
        return transaction {
            ApiUser.all().toList()
        }
    }

    override fun getById(id: Int): ApiUser {
        return transaction {
            ApiUser.findById(id) ?: throw NoSuchElementException("No such ApiUser")
        }
    }

    override fun updateById(id: Int, entity: LoginEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }

    fun getByName(name: String): ApiUser {
        return transaction {
            val user = ApiUser.find {
                ApiUsers.login eq name
            }.toList()

            when(user.size) {
                0 -> {
                    return@transaction user.first()
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
}