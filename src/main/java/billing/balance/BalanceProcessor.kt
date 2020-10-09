package billing.balance

import days360
import db.tables.*
import getFirstDayOfNextMonth
import getToday
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object BalanceProcessor {
    private val userBalanceHistoryCRUD = UserBalancesHistoryCRUD()
    private val balanceOperationsCRUD = BalanceOperationCRUD()
    private val withdrawCRUD = WithdrawsCRUD

    // Initiation for newly added user
    fun initBalance(user: User, amount: Int? = null) {
        transaction {
            UserBalance.new {
                this.user = user
                this.balance = amount ?: 0

                val currentDate = DateTime()
                scheduleNextMonthWithdrawing(user = user, end = currentDate.getFirstDayOfNextMonth(),
                        reason = WithdrawReason.findById(1) ?: throw NoSuchElementException("No such reason"))
            }
        }
    }

    fun processedNewPayments(payments: List<Payment>) {
        transaction {
            payments.forEach {
                it.operation = makePaymentAndGetOperation(
                        it.contractNumber,
                        it.totalAmount
                )
            }
        }
    }

    private fun makePaymentAndGetOperation(user: User, amount: Int, isWithdrawing: Boolean = false): BalanceOperation {
        return transaction {
            UsersBalancesCRUD.getByUserId(user.id.value)?.let {
                val operation = if (isWithdrawing) {
                    it.balance -= amount
                    balanceOperationsCRUD.addAndGet(user, 1)
                } else {
                    it.balance += amount
                    balanceOperationsCRUD.addAndGet(user, 2)
                }

                userBalanceHistoryCRUD.add(operation.id.value, user, it.balance)

                operation
            } ?: throw NoSuchElementException("Can't find such balance")
        }
    }

    private fun scheduleNextMonthWithdrawing(user: User, begin: DateTime = DateTime().getToday(), end: DateTime, reason: WithdrawReason): Withdraw {
        val activeDays = begin.days360(end)
        val amount = if (activeDays == 30)
            user.tariff.price
        else
            user.tariff.price / 30 * activeDays

        return withdrawCRUD.addAndGet(user, amount, reason, begin, end, end)
    }

    fun proceedScheduledWithdrawForUser(user: User, date: DateTime) {
        withdrawCRUD.getScheduledWithdrawsForUser(user, date).forEach {
            proceedWithdraw(it)
        }
    }

    fun proceedScheduledWithdraws(date: DateTime = DateTime()) {
        withdrawCRUD.getScheduledWithdraws(date).forEach {
            proceedWithdraw(it)
        }
    }

    private fun proceedWithdraw(withdraw: Withdraw) {
        withdraw.operation = makePaymentAndGetOperation(withdraw.user, withdraw.amount, true)
        if (!withdraw.user.isSuspended)
            scheduleNextMonthWithdrawing(withdraw.user, withdraw.endDate, withdraw.endDate.getFirstDayOfNextMonth(), withdraw.reason)
    }

    fun scheduleWithdraws(user: User, beginOfPeriod: DateTime, endOfPeriod: DateTime) {
        scheduleNextMonthWithdrawing(user, beginOfPeriod, endOfPeriod,
                WithdrawReason.findById(1) ?: throw NoSuchElementException("No such reason"))
    }

    // Removing scheduled withdraws
    // Should be called in suspend activation
    fun removeScheduledWithdraws(user: User, date: DateTime) {
        withdrawCRUD.getScheduledWithdrawsForUser(user, date).forEach {
            it.delete()
        }
    }
}