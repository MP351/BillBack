package db.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

object UserBalancesHistory: IdTable<Int>() {
    override val id = reference("id", BalanceOperations)
    val userId = reference("user_id", Users)
    val amount = integer("amount")
}

class UserBalanceHistory(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<UserBalanceHistory>(UserBalancesHistory)
    var user by User referencedOn UserBalancesHistory.userId
    var amount by UserBalancesHistory.amount
}

class UserBalancesHistoryCRUD {
    fun add(opcode: Int, user: User, balance: Int): EntityID<Int> {
        return addAndGet(opcode, user, balance).id
    }

    fun addAndGet(opcode: Int, user: User, balance: Int): UserBalanceHistory {
        return UserBalanceHistory.new(opcode) {
            this.user = user
            this.amount = balance
        }
    }
}