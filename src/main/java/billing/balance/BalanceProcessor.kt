package billing.balance

import days360
import db.tables.*
import getFirstDayOfNextMonth
import getToday
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import kotlin.NoSuchElementException

object BalanceProcessor {
    private val userBalanceCRUD = UsersBalancesCRUD
    private val userBalanceHistoryCRUD = UserBalancesHistoryCRUD()
    private val balanceOperationsCRUD = BalanceOperationCRUD()
    private val withdrawCRUD = WithdrawsCRUD
    private val suspendCRUD = SuspendsCRUD

    // Initiation for newly added user
    fun initBalance(user: User, amount: Int? = null) {
        transaction {
            UserBalance.new(user.id.value) {
                balance = amount ?: 0

                val currentDate = DateTime()
                scheduleNextMonthWithdrawing(user = user, end = currentDate.getFirstDayOfNextMonth(),
                        reason = WithdrawReason.findById(1) ?: throw NoSuchElementException("No such reason"))
            }
        }
    }

    fun processedNewPayments(payments: List<Payment>) {
        payments.forEach {
            it.operation = makePaymentAndGetOperation(
                    it.contractNumber,
                    it.totalAmount
            )
        }
    }

    private fun makePaymentAndGetOperation(user: User, amount: Int, isWithdrawing: Boolean = false): BalanceOperation {
        return transaction {
            val currentBalance = UserBalance[user.id]

            val operation = if (isWithdrawing) {
                currentBalance.balance -= amount
                balanceOperationsCRUD.addAndGet(user, 1)
            } else {
                currentBalance.balance += amount
                balanceOperationsCRUD.addAndGet(user, 2)
            }

            userBalanceHistoryCRUD.add(operation.id.value, user, currentBalance.balance)

            operation
        }
    }

    private fun getSuspendDaysCount(userId: Int, beginPeriod: DateTime, endPeriod: DateTime): Int {
        val suspends = suspendCRUD.getSuspendsForUserInPeriod(userId, beginPeriod, endPeriod)
        var count = 0

        suspends.forEach {
            val begin = if (it.beginDate.isBefore(beginPeriod))
                beginPeriod
            else
                it.beginDate

            val end = when {
                it.endDate == null -> endPeriod
                it.endDate!!.isAfter(endPeriod) -> endPeriod
                else -> it.endDate!!
            }

            count += begin.days360(end)
        }
        return count
    }

    private fun isSuspendActive(user: User, end: DateTime): Boolean {
        val suspend = Suspend.find {
            (Suspends.userId eq user.id) and
                    (Suspends.beginDate lessEq end) and
                    (Suspends.endDate.isNull() or (Suspends.endDate greater end))
        }
        return !suspend.empty()
    }

    private fun scheduleNextMonthWithdrawing(user: User, begin: DateTime = DateTime().getToday(), end: DateTime, reason: WithdrawReason): Withdraw {
        val activeDays = begin.days360(end)
        val amount = if (activeDays == 30)
            user.tariff.price
        else
            user.tariff.price / 30 * activeDays

        return Withdraw.new {
            this.user = user
            this.amount = amount
            this.reason = reason
            this.beginDate = begin
            this.endDate = end
            this.scheduledDate = end
        }
    }

    fun proceedScheduledWithdraws(date: DateTime = DateTime()) {
        val reason = WithdrawReason.findById(1) ?: throw NoSuchElementException("No such reason")
        Withdraw.find {
            Withdraws.scheduledDate eq date
        }.forEach {
            it.operation = makePaymentAndGetOperation(it.user, it.amount, true)
            scheduleNextMonthWithdrawing(it.user, it.endDate, it.endDate.getFirstDayOfNextMonth(), reason)
        }
    }

    fun scheduleWithdraws(user: User, beginOfPeriod: DateTime, endOfPeriod: DateTime) {
        scheduleNextMonthWithdrawing(user, beginOfPeriod, endOfPeriod,
                WithdrawReason.findById(1) ?: throw NoSuchElementException("No such reason"))
    }

    // Removing scheduled withdraws
    // Should be called in suspend activation
    fun removeScheduledWithdraws(user: User, date: DateTime) {
        Withdraw.find {
            Withdraws.userId eq user.id and
                    (Withdraws.scheduledDate eq date)
        }.toList().forEach {
            it.delete()
        }
    }
}