package db.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.date
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

class BalanceOperationCRUD {
    fun add(user: User, typeId: Int): EntityID<Int> {
        return addAndGet(user, typeId).id
    }

    fun addAndGet(user: User, typeId: Int): BalanceOperation {
        return BalanceOperation.new {
            this.user = user
            this.date = DateTime(System.currentTimeMillis())
            this.type = BalanceOperationType.findById(typeId) ?: throw NoSuchElementException("No such type")
        }
    }

    fun getAll(): List<BalanceOperation> {
        return BalanceOperation.all().toList()
    }

    fun getById(id: Int): BalanceOperation {
        return BalanceOperation.findById(id) ?: throw NoSuchElementException("No such operation")
    }
}