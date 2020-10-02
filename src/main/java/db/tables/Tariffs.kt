package db.tables

import TariffEntity
import db.DbQueries
import db.SpeedLimit
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

object Tariffs: IntIdTable() {
    val tariffName: Column<String> = varchar("name", 20).uniqueIndex()
    val price: Column<Int> = integer("price")
    val speedLimit: Column<String> = varchar("speed_limit", 10)
}

class Tariff(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<Tariff>(Tariffs)
    var name by Tariffs.tariffName
    var price by Tariffs.price
    var speedLimit: SpeedLimit by Tariffs.speedLimit.transform({
        "${it.downlink}/${it.uplink}"
    }, {
        val speeds = it.split("/")
        SpeedLimit(speeds[0], speeds[1])
    })

    override fun toString(): String {
        return "${id.value} $name $price $speedLimit"
    }
}

object TariffCRUD: DbQueries<TariffEntity, Tariff> {
    override fun add(entity: TariffEntity): EntityID<Int> {
        return transaction {
            Tariff.new {
                name = entity.name
                price = entity.price
                speedLimit = entity.speedLimits
            }.id
        }
    }

    override fun getAll(): List<Tariff> {
        return transaction {
            Tariff.all().toList()
        }
    }

    override fun getById(id: Int): Tariff {
        return transaction {
            Tariff.findById(id) ?: throw NoSuchElementException("No such tariff")
        }
    }

    override fun updateById(id: Int, entity: TariffEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }

    fun getTariffForUser(contractNumber: Int): Tariff {
        return transaction {
            User.findById(contractNumber)?.tariff ?: throw NoSuchElementException("No user or tariff with such id")
        }
    }
}
