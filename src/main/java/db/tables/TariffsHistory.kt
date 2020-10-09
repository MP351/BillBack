package db.tables

import TariffHistoryEntity
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object TariffsHistory: IntIdTable() {
    val tariffId: Column<EntityID<Int>> = reference("tariff_id", Tariffs)
    val userId: Column<EntityID<Int>> = reference("user_id", Users)
    val beginDate: Column<DateTime> = date("begin_date")
    val endDate: Column<DateTime?> = date("end_date").nullable()
}

class TariffHistory(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<TariffHistory>(TariffsHistory)
    var tariff by Tariff referencedOn TariffsHistory.tariffId
    var user by User referencedOn TariffsHistory.userId
    var beginDate by TariffsHistory.beginDate
    var endDate by TariffsHistory.endDate

    override fun toString(): String {
        return "${tariff.name} ${user.lastName} $beginDate $endDate"
    }
}

object TariffsHistoryCRUD {
    fun add(entity: TariffHistoryEntity): EntityID<Int> {
        return TariffHistory.new {
            tariff = Tariff.findById(entity.tariffId) ?: throw NoSuchElementException("No such tariff")
            user = User.findById(entity.userId) ?: throw NoSuchElementException("No such user")
            beginDate = DateTime(entity.beginDate)
            endDate = DateTime(entity.endDate)
        }.id
    }

    fun getAll(): List<TariffHistory> {
        return TariffHistory.all().toList()
    }

    fun getById(id: Int): TariffHistory {
        return TariffHistory.findById(id)
                ?: throw NoSuchElementException("No such user")
    }

    fun getTariffHistoryForUser(id: Int): List<TariffHistory> {
        return transaction {
            TariffHistory.find {
                TariffsHistory.userId eq id
            }.toList()
        }
    }
}