package db.tables

import WithdrawReasonEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction

object WithdrawReasons: IntIdTable() {
    val name = varchar("name", 50)
}

class WithdrawReason(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<WithdrawReason>(WithdrawReasons)
    var name by WithdrawReasons.name
}

object WithdrawReasonsCRUD {
    fun add(entity: WithdrawReasonEntity): EntityID<Int> {
        return WithdrawReason.new {
            name = entity.name
        }.id
    }

    fun getAll(): List<WithdrawReason> {
        return WithdrawReason.all().toList()
    }

    fun getById(id: Int): WithdrawReason {
        return WithdrawReason.findById(id) ?: throw NoSuchElementException("No such element")
    }
}