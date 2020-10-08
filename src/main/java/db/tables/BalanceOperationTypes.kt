package db.tables

import BalanceOperationTypeEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

object BalanceOperationTypes: IntIdTable() {
    val name: Column<String> = varchar("type_name", 100)
}

class BalanceOperationType(id: EntityID<Int>): IntEntity(id){
    companion object: IntEntityClass<BalanceOperationType>(BalanceOperationTypes)
    var name by BalanceOperationTypes.name
}

object BalanceOperationTypeCRUD: DbQueries<BalanceOperationTypeEntity, BalanceOperationType> {
    override fun add(entity: BalanceOperationTypeEntity): EntityID<Int> {
        return transaction {
            BalanceOperationType.new {
                name = entity.name
            }
        }.id
    }

    override fun getAll(): List<BalanceOperationType> {
        return transaction {
            BalanceOperationType.all().toList()
        }
    }

    override fun getById(id: Int): BalanceOperationType {
        return transaction {
            BalanceOperationType.findById(id) ?: throw NoSuchElementException("No such type")
        }
    }

    override fun updateById(id: Int, entity: BalanceOperationTypeEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }
}