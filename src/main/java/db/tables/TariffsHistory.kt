package db.tables

import TariffHistoryEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object TariffsHistory: IntIdTable() {
    val tariffId: Column<EntityID<Int>> = reference("tariff_id", Tariffs)
    val userId: Column<EntityID<Int>> = reference("user_id", Users)
    val beginDate: Column<DateTime> = date("begin_date")
    val endDate: Column<DateTime> = date("end_date")
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

object TariffsHistoryCRUD: DbQueries<TariffHistoryEntity, TariffHistory> {
    override fun add(entity: TariffHistoryEntity): EntityID<Int> {
        return transaction {
            TariffHistory.new {
                tariff = Tariff.findById(entity.tariffId) ?: throw NoSuchElementException("No such tariff")
                user = User.findById(entity.userId) ?: throw NoSuchElementException("No such user")
                beginDate = DateTime(entity.beginDate)
                endDate = DateTime(entity.endDate)
            }.id
        }
    }

    override fun getAll(): List<TariffHistory> {
        return transaction {
            TariffHistory.all().toList()
        }
    }

    override fun getById(id: Int): TariffHistory {
        return transaction {
            TariffHistory.findById(id)
                    ?: throw NoSuchElementException("No such user")
        }
    }

    override fun updateById(id: Int, entity: TariffHistoryEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }

    // begin and end dates should be first days of months yyyy-MM-dd
    fun getTariffHistoryForUserInPeriod(id: Int, begin: DateTime, end: DateTime): List<TariffHistory> {
        return transaction {
            TariffHistory.find {
                (TariffsHistory.userId eq id) and
                        ((TariffsHistory.endDate greaterEq begin) and (TariffsHistory.endDate less end)) or
                        ((TariffsHistory.beginDate greaterEq begin) and (TariffsHistory.beginDate less end)) or
                        ((TariffsHistory.beginDate lessEq begin) and (TariffsHistory.endDate greater begin))
            }.toList()
        }
    }

    fun getTariffHistoryForUser(id: Int): List<TariffHistory> {
        return transaction {
            TariffHistory.find {
                TariffsHistory.userId eq id
            }.toList()
        }
    }
}