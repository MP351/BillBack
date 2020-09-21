package db.tables

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