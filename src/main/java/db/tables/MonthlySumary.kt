package db.tables

import MonthlySummaryIn
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object MonthlySummary: IntIdTable() {
    val monthDate: Column<DateTime> = date("month_date")
    val userId: Column<EntityID<Int>> = reference("user_id", Users)
    val sum: Column<Int> = integer("sum")
}

class MonthSummary(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<MonthSummary>(MonthlySummary)
    var monthDate by MonthlySummary.monthDate
    var user by User referencedOn MonthlySummary.userId
    var sum by MonthlySummary.sum

    override fun toString(): String {
        return "$monthDate ${user.lastName} $sum"
    }
}

object MonthlySummaryCRUD: DbQueries<MonthlySummaryIn, MonthSummary> {
    override fun add(entity: MonthlySummaryIn): EntityID<Int> {
        return transaction {
            MonthSummary.new {
                monthDate = DateTime(entity.monthDate)
                user = User.findById(entity.user_id) ?: throw NoSuchElementException("No such user")
                sum = entity.sum
            }.id
        }
    }

    override fun getAll(): List<MonthSummary> {
        return transaction {
            MonthSummary.all().toList()
        }
    }

    override fun getById(id: Int): MonthSummary {
        return transaction {
            MonthSummary.findById(id) ?: throw NoSuchElementException("No such report")
        }
    }

    override fun updateById(id: Int, entity: MonthlySummaryIn) {
        transaction {
            val sum = MonthSummary.findById(id) ?: throw NoSuchElementException("No such report")
            sum.sum = entity.sum
        }
    }

    override fun deleteById(id: Int) {
        transaction {
            when(val entity = MonthSummary.findById(id)) {
                null -> {
                    throw NoSuchElementException("Nu such report")
                }
                else -> {
                    entity.delete()
                }
            }
        }
    }

    fun getSummariesInPeriod(begin: DateTime, end: DateTime): List<MonthSummary> {
        return transaction {
            MonthSummary.find {
                (MonthlySummary.monthDate greaterEq begin) and
                        (MonthlySummary.monthDate less end)
            }.toList()
        }
    }

    fun getSummariesForUserInPeriod(id: Int, begin: DateTime, end: DateTime): List<MonthSummary> {
        return transaction {
            MonthSummary.find {
                (MonthlySummary.userId eq id) and
                        (MonthlySummary.monthDate greaterEq begin) and
                        (MonthlySummary.monthDate less end)
            }.toList()
        }
    }

    fun getMonthlySumForUserInMonth(id: Int, monthDate: DateTime): MonthSummary? {
        return transaction {
            val sums = MonthSummary.find {
                (MonthlySummary.userId eq id) and
                        (MonthlySummary.monthDate eq monthDate)
            }.toList()

            when(sums.size) {
                0 -> {
                    null
                }
                1 -> {
                    sums.first()
                }
                else -> {
                    throw IllegalArgumentException("Wrong amount of reports")
                }
            }
        }
    }
}