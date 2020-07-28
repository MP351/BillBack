package db.tables

import UserBalanceEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object UsersBalances: IntIdTable() {
    val userId: Column<EntityID<Int>> = reference("user_id", Users.id)
    val balance: Column<Int> = integer("balance")
    val lastOperationDate: Column<DateTime> = date("last_operation_date")
}

class UserBalance(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<UserBalance>(UsersBalances)
    var user by User referencedOn UsersBalances.userId
    var balance by UsersBalances.balance
    var lastOperationDate by UsersBalances.lastOperationDate
}

object UsersBalancesCRUD: DbQueries<UserBalanceEntity, UserBalance> {
    override fun add(entity: UserBalanceEntity): EntityID<Int> {
        return transaction {
            UserBalance.new {
                user = User.findById(entity.userId) ?: throw NoSuchElementException("No such user")
                balance = entity.balance
                lastOperationDate = DateTime(entity.lastOperationDate)
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
            balance.lastOperationDate = DateTime(entity.lastOperationDate)
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