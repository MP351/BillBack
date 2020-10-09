package db.tables

import WithdrawsEntity
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
    val scheduledDate: Column<DateTime> = date("scheduled_date")
}

class Withdraw(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<Withdraw>(Withdraws)
    var user by User referencedOn Withdraws.userId
    var operation by BalanceOperation optionalReferencedOn Withdraws.operationId
    var amount by Withdraws.amount
    var reason by WithdrawReason referencedOn Withdraws.reasonId
    var beginDate by Withdraws.beginDate
    var endDate by Withdraws.endDate
    var scheduledDate by Withdraws.scheduledDate
}

object WithdrawsCRUD {
    fun add(entity: WithdrawsEntity): EntityID<Int> {
        return addAndGet(
                User.findById(entity.userId) ?: throw NoSuchElementException("No such user"),
                entity.amount,
                WithdrawReason.findById(entity.reasonId) ?: throw NoSuchElementException("No such reason"),
                DateTime(entity.beginDate),
                DateTime(entity.endDate),
                DateTime(entity.scheduledDate)
        ).id
    }

    fun addAndGet(user: User, amount: Int, reason: WithdrawReason, beginDate: DateTime, endDate: DateTime, scheduledDate: DateTime): Withdraw {
        return Withdraw.new {
            this.user = user
            this.amount = amount
            this.reason = reason
            this.beginDate = beginDate
            this.endDate = endDate
            this.scheduledDate = scheduledDate
        }
    }

    fun getAll(): List<Withdraw> {
        return Withdraw.all().toList()
    }

    fun getById(id: Int): Withdraw {
        return Withdraw.findById(id) ?: throw NoSuchElementException("No such withdraw")
    }

    fun getUnprocessedWithdraws(): List<Withdraw> {
        return Withdraw.find {
            Withdraws.operationId.isNull() and
                    Withdraws.scheduledDate.lessEq(DateTime())
        }.toList()
    }

    fun getScheduledWithdrawsForUser(user: User, date: DateTime): List<Withdraw> {
        return Withdraw.find {
            Withdraws.userId eq user.id and
                    (Withdraws.scheduledDate eq date)
        }.toList()
    }

    fun getScheduledWithdraws(date: DateTime): List<Withdraw> {
        return Withdraw.find {
            Withdraws.scheduledDate eq date
        }.toList()
    }
}

