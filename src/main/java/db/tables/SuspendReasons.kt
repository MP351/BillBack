package db.tables

import SuspendReasonEntity
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object SuspendReasons: IntIdTable() {
    val name = varchar("reason_name", 100)
}

class SuspendReason(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<SuspendReason>(SuspendReasons)
    var name by SuspendReasons.name
}
object SuspendReasonsCRUD {
    fun add(entity: SuspendReasonEntity): EntityID<Int> {
        return SuspendReason.new {
            name = entity.name
        }.id
    }

    fun getAll(): List<SuspendReason> {
        return SuspendReason.all().toList()
    }

    fun getById(id: Int): SuspendReason {
        return SuspendReason.findById(id) ?: throw NoSuchElementException("No such reason")
    }
}