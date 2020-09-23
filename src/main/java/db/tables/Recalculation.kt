package db.tables

import RecalculationEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object Recalculations: IntIdTable() {
    val userId: Column<EntityID<Int>> = reference("user_id", Users)
    val amount: Column<Int> = integer("amount")
    val beginDate: Column<DateTime> = date("begin_date")
    val endDate: Column<DateTime> = date("end_date")
    val operationId: Column<EntityID<Int>?> = reference("operation_id", BalanceOperations).nullable()
    val reasonId: Column<EntityID<Int>> = reference("reason_id", RecalculationReasons)
}

class Recalculation(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<Recalculation>(Recalculations)
    var user by User referencedOn Recalculations.userId
    var amount by Recalculations.amount
    var beginDate by Recalculations.beginDate
    var endDate by Recalculations.endDate
    var operation by BalanceOperation optionalReferencedOn Recalculations.operationId
    var reason by RecalculationReason referencedOn Recalculations.reasonId
}

object RecalculationsCRUD: DbQueries<RecalculationEntity, Recalculation> {
    override fun add(entity: RecalculationEntity): EntityID<Int> {
        return transaction {
            Recalculation.new {
                user = User.findById(entity.userId) ?: throw NoSuchElementException("No such user")
                amount = entity.amount
                beginDate = DateTime(entity.beginDate)
                endDate = DateTime(entity.endDate)
                operation = BalanceOperation.findById(entity.operationId ?: -1)
                reason = RecalculationReason.findById(entity.reasonId) ?: throw NoSuchElementException("No such reason")
            }
        }.id
    }

    override fun getAll(): List<Recalculation> {
        return transaction {
            Recalculation.all().toList()
        }
    }

    override fun getById(id: Int): Recalculation {
        return transaction {
            Recalculation.findById(id) ?: throw NoSuchElementException("No such recalculation")
        }
    }

    override fun updateById(id: Int, entity: RecalculationEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }
}