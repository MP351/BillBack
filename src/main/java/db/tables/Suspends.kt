package db.tables

import SuspendEntity
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
import java.lang.IllegalArgumentException

object Suspends: IntIdTable() {
    val userId: Column<EntityID<Int>> = reference("user_id", Users)
    val beginDate: Column<DateTime> = date("begin_date")
    val endDate: Column<DateTime?> = date("end_date").nullable()
}

class Suspend(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<Suspend>(Suspends)
    var user by User referencedOn Suspends.userId
    var beginDate by Suspends.beginDate
    var endDate by Suspends.endDate

    override fun toString(): String {
        return "${user.lastName} $beginDate $endDate"
    }
}

object SuspendsCRUD: DbQueries<SuspendEntity, Suspend> {
    override fun add(entity: SuspendEntity): EntityID<Int> {
        return transaction {
            Suspend.new {
                user = User.findById(entity.userId)
                        ?: throw NoSuchElementException("No such user")
                beginDate = DateTime(entity.beginDate)
                endDate = when(entity.endDate) {
                    null -> {
                        null
                    }
                    else -> {
                        DateTime(entity.endDate)
                    }
                }
            }.id
        }
    }

    override fun getAll(): List<Suspend> {
        return transaction {
            Suspend.all().toList()
        }
    }

    override fun getById(id: Int): Suspend {
        return transaction {
            Suspend.findById(id) ?: throw NoSuchElementException("No such suspend")
        }
    }

    override fun updateById(id: Int, entity: SuspendEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }

    // begin and end dates should be first days of months yyyy-MM-dd
    fun getSuspendsForUserInPeriod(id: Int, begin: DateTime, end: DateTime): List<Suspend> {
        return transaction {
            Suspend.find {
                (Suspends.userId eq id) and
                        ((Suspends.endDate greaterEq begin) and (Suspends.endDate less end)) or
                        ((Suspends.beginDate greaterEq begin) and (Suspends.beginDate less end)) or
                        ((Suspends.beginDate lessEq begin) and (Suspends.endDate greaterEq begin))
            }.toList()
        }
    }

    fun getSuspendsForContract(contractNumber: Int): List<Suspend> {
        return transaction {
            Suspend.find {
                Suspends.userId eq contractNumber
            }.toList()
        }
    }

    fun getCurrentSuspension(contractNumber: Int): Suspend? {
        return transaction {
            val list = Suspend.find{
                (Suspends.userId eq contractNumber) and
                        (Suspends.endDate.isNull())
            }.toList()
            when(list.size) {
                0 -> {
                    null
                }
                1 -> {
                    list.first()
                }
                else -> {
                    throw IllegalArgumentException("Wrong amount of suspends")
                }
            }
        }
    }
}
