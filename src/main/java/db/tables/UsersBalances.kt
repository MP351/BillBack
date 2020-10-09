package db.tables

import UserBalanceEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

object UsersBalances: IntIdTable() {
    val userId: Column<EntityID<Int>> = reference("user_id", Users).uniqueIndex()
    val balance: Column<Int> = integer("balance")
}

class UserBalance(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<UserBalance>(UsersBalances)
    var user by User referencedOn UsersBalances.userId
    var balance by UsersBalances.balance
}

object UsersBalancesCRUD {
    fun add(entity: UserBalanceEntity): EntityID<Int> {
        return UserBalance.new {
            balance = entity.balance
        }.id
    }

    fun getAll(): List<UserBalance> {
        return UserBalance.all().toList()
    }

    fun getById(id: Int): UserBalance {
        return UserBalance.findById(id) ?: throw NoSuchElementException("Nu such balance record")
    }

    fun getByUserId(id: Int): UserBalance? {
        return transaction {
            UserBalance.find {
                UsersBalances.userId eq id
            }.toList().firstOrNull()
        }
    }
}