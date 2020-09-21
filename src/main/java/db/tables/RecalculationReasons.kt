package db.tables

import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object RecalculationReasons: IntIdTable() {
    val name: Column<String> = varchar("name", 20)
}

class RecalculationReason(id: EntityID<Int>): IntEntity(id){
    companion object: IntEntityClass<RecalculationReason>(RecalculationReasons)
    var name by RecalculationReasons.name
}

//object RecalculationReasonsCRUD: DbQueries