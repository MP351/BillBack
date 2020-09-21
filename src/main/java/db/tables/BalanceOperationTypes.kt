package db.tables

import BalanceOperationTypeEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object BalanceOperationTypes: IntIdTable() {
    val name: Column<String> = varchar("name", 100)
}

class BalanceOperationType(id: EntityID<Int>): IntEntity(id){
    companion object: IntEntityClass<BalanceOperationType>(BalanceOperationTypes)
    var name by BalanceOperationTypes.name
}

object BalanceOperationCRUD: DbQueries<BalanceOperationTypeEntity, BalanceOperationType> {
    override fun add(entity: BalanceOperationTypeEntity): EntityID<Int> {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<BalanceOperationType> {
        TODO("Not yet implemented")
    }

    override fun getById(id: Int): BalanceOperationType {
        TODO("Not yet implemented")
    }

    override fun updateById(id: Int, entity: BalanceOperationTypeEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }
}