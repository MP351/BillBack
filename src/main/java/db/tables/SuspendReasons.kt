package db.tables

import SuspendReasonEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction

object SuspendReasons: IntIdTable() {
    val name = varchar("name", 100)
}

class SuspendReason(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<SuspendReason>(SuspendReasons)
    var name by SuspendReasons.name
}
object SuspendReasonsCRUD: DbQueries<SuspendReasonEntity, SuspendReason> {
    override fun add(entity: SuspendReasonEntity): EntityID<Int> {
        return transaction {
            SuspendReason.new {
                name = entity.name
            }
        }.id
    }

    override fun getAll(): List<SuspendReason> {
        return SuspendReason.all().toList()
    }

    override fun getById(id: Int): SuspendReason {
        return SuspendReason.findById(id) ?: throw NoSuchElementException("No such reason")
    }

    override fun updateById(id: Int, entity: SuspendReasonEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }
}