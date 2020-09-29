package db.tables

import WithdrawsEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object Withdraws: IntIdTable() {
    val userId: Column<EntityID<Int>> = reference("user_id", Users)
    val operationId: Column<EntityID<Int>?> = reference("operation_id", BalanceOperations).nullable()
    val amount: Column<Int> = integer("amount")
    val reasonId: Column<EntityID<Int>> = reference("reason_id", WithdrawReasons)
    val beginDate: Column<DateTime> = date("begin_date")
    val endDate: Column<DateTime> = date("end_date")
    val scheduled_date: Column<DateTime> = date("scheduled_date")
}

class Withdraw(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<Withdraw>(Withdraws)
    var user by User referencedOn Withdraws.userId
    var operation by BalanceOperation optionalReferencedOn Withdraws.operationId
    var amount by Withdraws.amount
    var reason by WithdrawReason referencedOn Withdraws.reasonId
    var beginDate by Withdraws.beginDate
    var endDate by Withdraws.endDate
    var scheduledDate by Withdraws.scheduled_date
}

object WithdrawsCRUD {
    fun add(entity: WithdrawsEntity): EntityID<Int> {
        return transaction {
            Withdraw.new {
                user = User.findById(entity.userId) ?: throw NoSuchElementException("No such user")
                operation = BalanceOperation.findById(entity.operationId ?: -1)
                amount = entity.amount
                reason = WithdrawReason.findById(entity.reasonId) ?: throw NoSuchElementException("No such reason")
                beginDate = DateTime(entity.beginDate)
                endDate = DateTime(entity.endDate)
                scheduledDate = DateTime(entity.scheduledDate)
            }
        }.id
    }

    fun getAll(): List<Withdraw> {
        return transaction {
            Withdraw.all().toList()
        }
    }

    fun getById(id: Int): Withdraw {
        return transaction {
            Withdraw.findById(id) ?: throw NoSuchElementException("No such withdraw")
        }
    }

    fun getUnprocessedWithdraws(): List<Withdraw> {
        return Withdraw.find {
            Withdraws.operationId.isNull() and
                    Withdraws.scheduled_date.lessEq(DateTime())
        }.toList()
    }
}

