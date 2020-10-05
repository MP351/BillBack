package db.tables

import SuspendEntityNew
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.lang.IllegalArgumentException

object Suspends: IntIdTable() {
    val userId: Column<EntityID<Int>> = reference("user_id", Users)
    val beginDate: Column<DateTime> = date("begin_date")
    val endDate: Column<DateTime?> = date("end_date").nullable()
    val reasonId: Column<EntityID<Int>> = reference("reason_id", SuspendReasons).default(SuspendReason.findById(1)!!.id)
    val notes: Column<String> = text("notes").default("")
    val isCompleted: Column<Boolean> = bool("is_completed").default(false)
}

class Suspend(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<Suspend>(Suspends)
    var user by User referencedOn Suspends.userId
    var beginDate by Suspends.beginDate
    var endDate by Suspends.endDate
    var reason by SuspendReason referencedOn Suspends.reasonId
    var notes by Suspends.notes
    var isCompleted by Suspends.isCompleted

    override fun toString(): String {
        return "${user.lastName} $beginDate $endDate"
    }
}

object SuspendsCRUD {
    fun add(entity: SuspendEntityNew): EntityID<Int> {
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
                reason = SuspendReason.findById(entity.reason_id) ?: throw NoSuchElementException("No such reason")
                notes = entity.comment
            }.id
        }
    }

    fun getAll(): List<Suspend> {
        return transaction {
            Suspend.all().toList()
        }
    }

    fun getById(id: Int): Suspend {
        return transaction {
            Suspend.findById(id) ?: throw NoSuchElementException("No such suspend")
        }
    }

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
                        (Suspends.beginDate.lessEq(DateTime())) and
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

    fun getEndingSuspendForUser(user: User, dateTime: DateTime): Suspend? {
        return Suspend.find {
            (Suspends.userId eq user.id) and (Suspends.endDate eq dateTime)
        }.toList().first()
    }

    fun getSuspendsForActiveUnsuspendedUsers(date: DateTime): List<Suspend> {
        return Suspend.wrapRows(
                Suspends.innerJoin(Users)
                    .slice(Suspends.columns)
                    .select {
                        Users.isActive eq true and
                                (Users.isSuspended eq false) and
                                (Suspends.beginDate eq date)
                    }.withDistinct()).toList()
    }

    fun getLastSuspendEnding(user: User, date: DateTime): DateTime? {
        return Suspend.find {
            Suspends.userId eq user.id  and
                    (Suspends.endDate lessEq date)
        }.maxBy {
            Suspends.endDate
        }?.endDate
    }

    fun getSuspendsScheduledForResume(date: DateTime): List<Suspend> {
        return Suspend.find {
            Suspends.endDate eq date
        }.toList()
    }
}
