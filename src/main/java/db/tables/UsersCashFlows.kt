package db.tables

import UserCashFlowEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object UsersCashFlows: IntIdTable() {
    val operationDate: Column<DateTime> = date("operation_date")
    val amount: Column<Int> = integer("amount")
    val isWithdraw: Column<Boolean> = bool("is_withdraw")
    val reasonId: Column<EntityID<Int>> = reference("reason_id", CashFlowReasons.id)
}

class UserCashFlow(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<UserCashFlow>(UsersCashFlows)
    var operationDate by UsersCashFlows.operationDate
    var amount by UsersCashFlows.amount
    var isWithdraw by UsersCashFlows.isWithdraw
    var reason by CashFlowReason referencedOn UsersCashFlows.reasonId
}

object UserCashFlowsCRUD: DbQueries<UserCashFlowEntity, UserCashFlow> {
    override fun add(entity: UserCashFlowEntity): EntityID<Int> {
        return transaction {
            UserCashFlow.new {
                operationDate = DateTime()
                amount = entity.amount
                isWithdraw = entity.isWithdraw
                reason = CashFlowReason.findById(entity.reason) ?: throw NoSuchElementException("NoSuch reason")
            }.id
        }
    }

    override fun getAll(): List<UserCashFlow> {
        return UserCashFlow.all().toList()
    }

    override fun getById(id: Int): UserCashFlow {
        return UserCashFlow.findById(id) ?: throw NoSuchElementException("No such record")
    }

    override fun updateById(id: Int, entity: UserCashFlowEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }
}