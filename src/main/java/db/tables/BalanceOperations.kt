package db.tables

import BalanceOperationEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object BalanceOperations: IntIdTable() {
    val userId: Column<EntityID<Int>> = reference("user_id", Users)
    val date: Column<DateTime> = date("date")
    val typeId: Column<EntityID<Int>> = reference("type_id", BalanceOperationTypes)
}

class BalanceOperation(id: EntityID<Int>): IntEntity(id){
    companion object: IntEntityClass<BalanceOperation>(BalanceOperations)
    var user by User referencedOn BalanceOperations.userId
    var date by BalanceOperations.date
    var type by BalanceOperationType referencedOn BalanceOperations.typeId
}

object BalanceOperationCRUD: DbQueries<BalanceOperationEntity, BalanceOperation> {
    override fun add(entity: BalanceOperationEntity): EntityID<Int> {
        return transaction {
            BalanceOperation.new {
                user = User.findById(entity.userId) ?: throw NoSuchElementException("No such user")
                date = DateTime(entity.date)
                type = BalanceOperationType.findById(entity.typeId) ?: throw NoSuchElementException("No such type")
            }
        }.id
    }

    override fun getAll(): List<BalanceOperation> {
        return transaction {
            BalanceOperation.all().toList()
        }
    }

    override fun getById(id: Int): BalanceOperation {
        return transaction {
            BalanceOperation.findById(id) ?: throw NoSuchElementException("No such operation")
        }
    }

    override fun updateById(id: Int, entity: BalanceOperationEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }
}