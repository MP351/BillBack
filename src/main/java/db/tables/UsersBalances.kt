package db.tables

import UserBalanceEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

object UsersBalances: IdTable<Int>() {
    override val id: Column<EntityID<Int>> = reference("id", BalanceOperations)
    val balance: Column<Int> = integer("balance")
}

class UserBalance(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<UserBalance>(UsersBalances)
    var balance by UsersBalances.balance
}

object UsersBalancesCRUD: DbQueries<UserBalanceEntity, UserBalance> {
    override fun add(entity: UserBalanceEntity): EntityID<Int> {
        return transaction {
            UserBalance.new {
                balance = entity.balance
            }
        }.id
    }

    override fun getAll(): List<UserBalance> {
        return transaction {
            UserBalance.all()
        }.toList()
    }

    override fun getById(id: Int): UserBalance {
        return transaction {
            UserBalance.findById(id) ?: throw NoSuchElementException("Nu such balance record")
        }
    }

    override fun updateById(id: Int, entity: UserBalanceEntity) {
        transaction {
            val balance = UserBalance.findById(id) ?: throw NoSuchElementException("No such balance record")
            balance.balance = entity.balance
        }
    }

    override fun deleteById(id: Int) {
        transaction {
            val balance = UserBalance.findById(id) ?: throw NoSuchElementException("No such balance record")
            balance.delete()
        }
    }

    fun getByUserId(id: Int): UserBalance {
        return transaction {
            UserBalance.findById(id) ?: throw NoSuchElementException("No such balance record")
        }
    }
}