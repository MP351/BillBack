package db.tables

import UserEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

object Users: IntIdTable() {
    val firstName: Column<String> = text("first_name")
    val lastName: Column<String> = text("last_name")
    val fatherName: Column<String> = text("father_name")
    val tariffId: Column<EntityID<Int>> = reference("tariff_id", Tariffs)
    val isActive: Column<Boolean> = bool("is_active")
}

class User(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<User>(Users)
    var firstName by Users.firstName
    var lastName by Users.lastName
    var fatherName by Users.fatherName
    var tariffId by Tariff referencedOn Users.tariffId
    var isActive by Users.isActive

    override fun toString(): String {
        return "$id $firstName $lastName ${tariffId.name} $isActive"
    }
}

object UsersCRUD: DbQueries<UserEntity, User> {
    override fun add(entity: UserEntity): EntityID<Int> {
        return transaction {
            User.new(entity.contractNumber) {
                firstName = entity.firstName
                lastName = entity.lastName
                fatherName = entity.fatherName
                tariffId = Tariff.findById(entity.tariffId)
                        ?: throw NoSuchElementException("No such tariff")
                isActive = entity.isActive
            }
        }.id
    }

    override fun getAll(): List<User> {
        return transaction {
            User.all().toList()
        }
    }

    override fun getById(id: Int): User {
        return transaction {
            User.findById(id)
                    ?: throw NoSuchElementException("No such user")
        }
    }

    override fun updateById(id: Int, entity: UserEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }
}