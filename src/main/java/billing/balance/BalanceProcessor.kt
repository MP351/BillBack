package billing.balance

import db.tables.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.util.*

object BalanceProcessor {
    private val userBalanceCRUD = UsersBalancesCRUD
    private val userBalanceHistoryCRUD = UserBalancesHistoryCRUD()
    private val balanceOperationsCRUD = BalanceOperationCRUD()
    private val withdrawCRUD = WithdrawsCRUD
    private val suspendCRUD = SuspendsCRUD

    fun initBalance(id: Int) {
        transaction {
            UserBalance.new(id) {
                balance = 0

                // TODO: init withdraws
            }
        }
    }

    fun proceedNewPayments(payments: List<Payment>) {
        payments.forEach {
            it.operation = addPaymentOntoBalance(
                    it.contractNumber.id.value,
                    it.totalAmount
            )
        }
    }

    private fun addPaymentOntoBalance(userId: Int, amount: Int): BalanceOperation {
        return transaction {
            val currentBalance = userBalanceCRUD.getByUserId(userId)
            currentBalance.balance += amount

            val operation = balanceOperationsCRUD.addAndGet(userId, 1)
            userBalanceHistoryCRUD.add(operation.id.value, userId, currentBalance.balance)

            operation
        }
    }

    fun withdrawMonthlyPaymentFromBalances() {
        transaction {
            val withdraws = withdrawCRUD.getUnprocessedWithdraws()

            withdraws.forEach {
                val sum = it.amount - it.amount / 30 * getSuspendDaysCount(it.user.id.value, it.beginDate, it.endDate)
                it.operation = withdrawPaymentFromBalance(
                        it.user.id.value,
                        sum
                )

                Withdraw.new {
                    user = it.user
                    amount = it.amount
                    reason = it.reason
                    beginDate = it.endDate
                    val nextMonth = it.endDate.plusMonths(1)
                    endDate = nextMonth
                    scheduledDate = nextMonth
                }
                it.amount = sum
            }
        }
    }

    private fun withdrawPaymentFromBalance(userId: Int, amount: Int): BalanceOperation {
        return transaction {
            val currentBalance = userBalanceCRUD.getByUserId(userId)
            currentBalance.balance -= amount

            val operation = balanceOperationsCRUD.addAndGet(userId, 1)
            userBalanceHistoryCRUD.add(operation.id.value, userId, currentBalance.balance)

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

            count += get360days(begin, end)
        }
        return count
    }

    // Calculating US/NASD method
    private fun get360days(begin: DateTime, end: DateTime): Int {
        val beginYear = begin.year().get()
        val beginMonth = begin.monthOfYear().get()
        var beginDay = begin.dayOfMonth().get()

        val endYear = end.year().get()
        val endMonth = end.monthOfYear().get()
        var endDay = end.dayOfMonth().get()

        val calendar = Calendar.getInstance().apply {
            set(beginYear, beginMonth, beginDay)
        }

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        if (beginMonth == 1 && endMonth == 1
                && beginDay == daysInMonth
                && endDay == daysInMonth) {
            endDay = 30
        }

        when(beginDay) {
            31, daysInMonth -> {
                beginDay = 30
                if (endDay == 31)
                    endDay = 30
            }
        }

        return (endYear - beginYear) * 360 + (endMonth - beginMonth) * 30 + (endDay - beginDay)
    }

    private fun scheduleNextMonthWithdrawing(userId: Int) {
        Withdraw.new {

        }
    }
}