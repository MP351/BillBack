package db.tables

import UserEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object Users: IntIdTable() {
    val firstName: Column<String> = text("first_name")
    val lastName: Column<String> = text("last_name")
    val fatherName: Column<String> = text("father_name")
    val tariffId: Column<EntityID<Int>> = reference("tariff_id", Tariffs)
    val tariffActivationDate: Column<DateTime> = date("tariff_activation_date")
    val isSuspended: Column<Boolean> = bool("is_suspended")
    val isActive: Column<Boolean> = bool("is_active")
    val lastWatchedTime: Column<DateTime> = date("last_watched_time")
}

class User(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<User>(Users)
    var firstName by Users.firstName
    var lastName by Users.lastName
    var fatherName by Users.fatherName
    var tariff by Tariff referencedOn Users.tariffId
    var tariffActivationDate by Users.tariffActivationDate
    var isSuspended by Users.isSuspended
    var isActive by Users.isActive
    var lastWatchedTime by Users.lastWatchedTime

    override fun toString(): String {
        return "$id $firstName $lastName ${tariff.name} $isActive"
    }
}

object UsersCRUD {
    fun add(entity: UserEntity): EntityID<Int> {
        return transaction {
            User.new(entity.contractNumber) {
                firstName = entity.firstName
                lastName = entity.lastName
                fatherName = entity.fatherName
                tariff = Tariff.findById(entity.tariffId)
                        ?: throw NoSuchElementException("No such tariff")
                tariffActivationDate = DateTime(entity.tariffActivationDate)
                isSuspended = entity.isSuspended
                isActive = entity.isActive
                lastWatchedTime = DateTime()
            }
        }.id
    }

    fun addAndGet(firstName: String, lastName: String, fatherName: String,
                  tariffId: Int, isSuspended: Boolean = true, isActive: Boolean = true): User {
        return transaction {
            User.new {
                this.firstName = firstName
                this.lastName = lastName
                this.fatherName = fatherName
                this.tariff = Tariff.findById(tariffId)
                        ?: throw NoSuchElementException("No such tariff")
                this.tariffActivationDate = DateTime()
                this.isSuspended = isSuspended
                this.isActive = isActive
                this.lastWatchedTime = DateTime()
            }
        }
    }

    fun getAll(): List<User> {
        return transaction {
            User.all().toList()
        }
    }

    fun getById(id: Int): User {
        return transaction {
            User.findById(id)
                    ?: throw NoSuchElementException("No such user")
        }
    }
}