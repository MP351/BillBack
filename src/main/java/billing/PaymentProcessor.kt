package billing

import MonthlySummary
import MonthlySummaryIn
import PaymentEntity
import db.tables.InvalidPaymentsCRUD
import db.tables.MonthlySummaryCRUD
import db.tables.Payment
import db.tables.PaymentsCRUD
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object PaymentProcessor {
    private val paymentCRUD = PaymentsCRUD
    private val invalidPaymentCRUD = InvalidPaymentsCRUD
    private val monthlySummaryCRUD = MonthlySummaryCRUD

    fun addPayment(payment: PaymentEntity): EntityID<Int> {
        return try {
            val id = transaction {
                paymentCRUD.add(payment)
            }
            updateMonthlySumForUser(payment.contractNumber, payment.dateTime)

            return id
        } catch (e: NoSuchElementException) {
            invalidPaymentCRUD.add(payment)
        }
    }

    fun getPaymentsForUserInPeriod(contractNumber: Int, beginDate: DateTime, endDate: DateTime): List<Payment> {
        return transaction {
            paymentCRUD.getPaymentsForUserInPeriod(contractNumber, beginDate, endDate)
        }
    }

    fun getBalanceForUserInMonth(contractNumber: Int, monthDate: DateTime): MonthlySummary {
        val date = formatDate(monthDate)
        TODO()
    }

    // param @monthDate should be a first day of month
    fun getBalancesInMonth(monthDate: DateTime): List<MonthlySummary> {
        TODO()
    }

    private fun updateMonthlySumForUser(contractNumber: Int, timestamp: Long) {
        val date = formatDate(DateTime(timestamp))

        try {
            val cur = monthlySummaryCRUD.getMonthlySumForUserInMonth(contractNumber, date)
            val sum = calculateMonthlySumForUser(contractNumber, date)

            when(cur) {
                null -> {
                    monthlySummaryCRUD.add(sum)
                }
                else -> {
                    monthlySummaryCRUD.updateById(cur.id.value, sum)
                }
            }
        } catch (ils: IllegalArgumentException) {

        } catch (e: Exception) {

        }
    }

    private fun calculateMonthlySumForUser(contractNumber: Int, date: DateTime): MonthlySummaryIn {
        TODO()
    }

    private fun formatDate(date: DateTime): DateTime {
        return DateTime(date.year, date.monthOfYear, 1, 0, 0)
    }
}