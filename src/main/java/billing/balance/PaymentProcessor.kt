package billing.balance

import PaymentEntity
import db.tables.InvalidPaymentsCRUD
import db.tables.Payment
import db.tables.PaymentsCRUD
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object PaymentProcessor {
    private val paymentCRUD = PaymentsCRUD
    private val invalidPaymentCRUD = InvalidPaymentsCRUD

    fun addPayment(payment: PaymentEntity): EntityID<Int> {
        return try {
            return paymentCRUD.add(payment)
        } catch (e: NoSuchElementException) {
            invalidPaymentCRUD.add(payment)
        }
    }

    fun getPaymentsForUserInPeriod(contractNumber: Int, beginDate: DateTime, endDate: DateTime): List<Payment> {
        return transaction {
            paymentCRUD.getPaymentsForUserInPeriod(contractNumber, beginDate, endDate)
        }
    }

    fun getUnprocessedPayments(): List<Payment> {
        return paymentCRUD.getUnprocessedPayments()
    }

    private fun getFirstDayOfMonth(date: DateTime): DateTime {
        return DateTime(date.year, date.monthOfYear, 1, 0, 0)
    }
}