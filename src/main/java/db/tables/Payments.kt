package db.tables

import PaymentEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object Payments: IntIdTable() {
    val operationDateTime: Column<DateTime> = datetime("operation_date")
    val div_number: Column<String> = varchar("division_number", 5)
    val cashierNumber: Column<String> = varchar("cashier_number", 10)
    val opcode: Column<String> = text("opcode").uniqueIndex()
    val contractNumber: Column<EntityID<Int>> = reference("user_id", Users)
    val userName: Column<String> = varchar("user_name", 100)
    val totalAmount: Column<Int> = integer("total_amount")
    val incomeAmount: Column<Int> = integer("income_amount")
    val commissionAmount: Column<Int> = integer("commission_amount")
    val operation_id: Column<EntityID<Int>?> = reference("operation_id", BalanceOperations).nullable()
}

class Payment(id: EntityID<Int>): IntEntity(id){
    companion object: IntEntityClass<Payment>(Payments)
    var operationDateTime by Payments.operationDateTime
    var divNumber by Payments.div_number
    var cashierNumber by Payments.cashierNumber
    var opcode by Payments.opcode
    var contractNumber by User referencedOn Payments.contractNumber
    var name by Payments.userName
    var totalAmount by Payments.totalAmount
    var incomeAmount by Payments.incomeAmount
    var commissionAmount by Payments.commissionAmount
    var operation by BalanceOperation optionalReferencedOn Payments.operation_id

    override fun toString(): String {
        return "$operationDateTime $divNumber $cashierNumber $opcode $contractNumber " +
                "$name $totalAmount $incomeAmount $commissionAmount"
    }
}

object PaymentsCRUD: DbQueries<PaymentEntity, Payment> {
    override fun add(entity: PaymentEntity): EntityID<Int> {
        return transaction {
            Payment.new {
                operationDateTime = DateTime(entity.dateTime)
                divNumber = entity.divisionNumber
                cashierNumber = entity.cashierNumber
                opcode = entity.operationCode
                contractNumber = User.findById(entity.contractNumber)
                        ?: throw NoSuchElementException("No user with such id")
                name = entity.name
                totalAmount = entity.totalAmount
                incomeAmount = entity.incomeAmount
                commissionAmount = entity.commissionAmount
                operation = BalanceOperation.findById(entity.operationId ?: -1)
            }.id
        }
    }

    override fun getAll(): List<Payment> {
        return transaction {
            Payment.all().toList()
        }
    }

    override fun getById(id: Int): Payment {
        return transaction {
            Payment.findById(id) ?: throw NoSuchElementException("No such payment")
        }
    }

    override fun updateById(id: Int, entity: PaymentEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }

    fun getPaymentsInPeriod(begin: DateTime, end: DateTime): List<Payment> {
        return transaction {
            Payment.find {
                (Payments.operationDateTime greaterEq begin) and
                        (Payments.operationDateTime less end)
            }.toList()
        }
    }

    fun getPaymentsForUser(id: Int): Payment {
        return transaction {
            Payment.findById(id) ?: throw NoSuchElementException("No such payment record")
        }
    }

    fun getPaymentsForUserInPeriod(id: Int, begin: DateTime, end: DateTime): List<Payment> {
        return transaction {
            Payment.find {
                (Payments.id eq id) and
                        (Payments.operationDateTime greaterEq begin) and
                        (Payments.operationDateTime less end)
            }.toList()
        }
    }

    fun getUnprocessedPayments(): List<Payment> {
        return transaction {
            Payment.find {
                Payments.operation_id.isNull()
            }.toList()
        }
    }
}
