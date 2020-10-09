package db.tables

import BalanceOperationTypeEntity
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object BalanceOperationTypes: IntIdTable() {
    val name: Column<String> = varchar("type_name", 100)
}

class BalanceOperationType(id: EntityID<Int>): IntEntity(id){
    companion object: IntEntityClass<BalanceOperationType>(BalanceOperationTypes)
    var name by BalanceOperationTypes.name
}

object BalanceOperationTypeCRUD {
    fun add(entity: BalanceOperationTypeEntity): EntityID<Int> {
        return BalanceOperationType.new {
            name = entity.name
        }.id
    }

    fun getAll(): List<BalanceOperationType> {
        return BalanceOperationType.all().toList()
    }

    fun getById(id: Int): BalanceOperationType {
        return BalanceOperationType.findById(id) ?: throw NoSuchElementException("No such type")
    }
}