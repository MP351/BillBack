package db.tables

import RecalculationReasonEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

object RecalculationReasons: IntIdTable() {
    val name: Column<String> = varchar("name", 20)
}

class RecalculationReason(id: EntityID<Int>): IntEntity(id){
    companion object: IntEntityClass<RecalculationReason>(RecalculationReasons)
    var name by RecalculationReasons.name
}

object RecalculationReasonsCRUD: DbQueries<RecalculationReasonEntity, RecalculationReason> {
    override fun add(entity: RecalculationReasonEntity): EntityID<Int> {
        return transaction {
            RecalculationReason.new {
                name = entity.name
            }.id
        }
    }

    override fun getAll(): List<RecalculationReason> {
        return transaction {
            RecalculationReason.all().toList()
        }
    }

    override fun getById(id: Int): RecalculationReason {
        return transaction {
            RecalculationReason.findById(id) ?: throw NoSuchElementException("No such reason")
        }
    }

    override fun updateById(id: Int, entity: RecalculationReasonEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }
}