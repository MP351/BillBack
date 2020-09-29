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
    fun add(opcode: Int, userId: Int, balance: Int): EntityID<Int> {
        return UserBalanceHistory.new(opcode) {
            user = User.findById(userId) ?: throw NoSuchElementException("No such user")
            amount = balance
        }.id
    }

    fun addAndGet(opcode: Int, userId: Int, balance: Int): UserBalanceHistory {
        return UserBalanceHistory.new(opcode) {
            user = User.findById(userId) ?: throw NoSuchElementException("No such user")
            amount = balance
        }
    }
}