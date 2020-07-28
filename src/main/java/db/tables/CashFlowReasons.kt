package db.tables

import UserCashFlowReasonEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

object CashFlowReasons: IntIdTable() {
     val reason: Column<String> = text("reason")
}

class CashFlowReason(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<CashFlowReason>(CashFlowReasons)
    var reason by CashFlowReasons.reason
}

object CashFlowReasonCRUD: DbQueries<UserCashFlowReasonEntity, CashFlowReason> {
    override fun add(entity: UserCashFlowReasonEntity): EntityID<Int> {
        return transaction {
            CashFlowReason.new {
                reason = entity.reason
            }
        }.id
    }

    override fun getAll(): List<CashFlowReason> {
        return CashFlowReason.all().toList()
    }

    override fun getById(id: Int): CashFlowReason {
        return CashFlowReason.findById(id) ?: throw NoSuchElementException("No such reason")
    }

    override fun updateById(id: Int, entity: UserCashFlowReasonEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }
}