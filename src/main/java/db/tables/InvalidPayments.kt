package db.tables

import PaymentEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object InvalidPayments: IntIdTable() {
    val operationDateTime: Column<DateTime> = datetime("operation_date")
    val div_number: Column<String> = varchar("division_number", 5)
    val cashierNumber: Column<String> = varchar("cashier_number", 10)
    val opcode: Column<String> = text("opcode").uniqueIndex()
    val contractNumber: Column<Int> = integer("user_id")
    val userName: Column<String> = varchar("user_name", 100)
    val totalAmount: Column<Int> = integer("total_amount")
    val incomeAmount: Column<Int> = integer("income_amount")
    val commissionAmount: Column<Int> = integer("commission_amount")
}

class InvalidPayment(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<InvalidPayment>(InvalidPayments)
    var operationDateTime by InvalidPayments.operationDateTime
    var divNumber by InvalidPayments.div_number
    var cashierNumber by InvalidPayments.cashierNumber
    var opcode by InvalidPayments.opcode
    var contractNumber by InvalidPayments.contractNumber
    var name by InvalidPayments.userName
    var totalAmount by InvalidPayments.totalAmount
    var incomeAmount by InvalidPayments.incomeAmount
    var commissionAmount by InvalidPayments.commissionAmount

    override fun toString(): String {
        return "$operationDateTime $divNumber $cashierNumber $opcode $contractNumber " +
                "$name $totalAmount $incomeAmount $commissionAmount"
    }
}

object InvalidPaymentsCRUD {
    fun add(entity: PaymentEntity): EntityID<Int> {
        return InvalidPayment.new {
            operationDateTime = DateTime(entity.dateTime)
            divNumber = entity.divisionNumber
            cashierNumber = entity.cashierNumber
            opcode = entity.operationCode
            contractNumber = entity.contractNumber
            name = entity.name
            totalAmount = entity.totalAmount
            incomeAmount = entity.incomeAmount
            commissionAmount = entity.commissionAmount
        }.id
    }

    fun getAll(): List<InvalidPayment> {
        return InvalidPayment.all().toList()
    }

    fun getById(id: Int): InvalidPayment {
        return InvalidPayment.findById(id)
                ?: throw NoSuchElementException("No such payments")
    }
}
