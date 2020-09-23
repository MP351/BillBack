package db.tables

import WithdrawReasonEntity
import db.DbQueries
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction

object WithdrawReasons: IntIdTable() {
    val name = varchar("name", 50)
}

class WithdrawReason(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<WithdrawReason>(WithdrawReasons)
    var name by WithdrawReasons.name
}

object WithdrawReasonsCRUD: DbQueries<WithdrawReasonEntity, WithdrawReason> {
    override fun add(entity: WithdrawReasonEntity): EntityID<Int> {
        return transaction {
            WithdrawReason.new {
                name = entity.name
            }.id
        }
    }

    override fun getAll(): List<WithdrawReason> {
        return transaction {
            WithdrawReason.all().toList()
        }
    }

    override fun getById(id: Int): WithdrawReason {
        return transaction {
            WithdrawReason.findById(id) ?: throw NoSuchElementException("No such element")
        }
    }

    override fun updateById(id: Int, entity: WithdrawReasonEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }
}