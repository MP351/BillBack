package billing

import TariffEntity
import TariffEntityDB
import UserEntity
import api.util.Response
import db.tables.TariffCRUD
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.transactions.transaction

object TariffsProcessor {
    private val tariffsCRUD = TariffCRUD
    fun getTariffs(): Response<List<TariffEntityDB>> {
        return try {
            val users = tariffsCRUD.getAll()
            val hm = HashMap<String, List<TariffEntityDB>>().apply {
                transaction {
                    put("tariffs", users.map {
                        TariffEntityDB(
                                it.id.value,
                                it.name,
                                it.price,
                                it.speedLimit
                        )
                    })
                }
            }

            Response.Success(HttpStatusCode.OK, hm)
        } catch (t: Throwable) {
            Response.Failure(HttpStatusCode.BadRequest, t.message.toString())
        }
    }

    fun getTariffForUser(user: UserEntity): Response<TariffEntityDB> {
        return try {
            val tariff = tariffsCRUD.getTariffForUser(user.contractNumber)
            val hm = HashMap<String, TariffEntityDB>().apply {
                put("tariff", TariffEntityDB(
                        tariff.id.value,
                        tariff.name,
                        tariff.price,
                        tariff.speedLimit
                ))
            }
            Response.Success(HttpStatusCode.OK, hm)
        } catch (t: Throwable) {
            Response.Failure(HttpStatusCode.BadRequest, t.message.toString())
        }
    }

    fun addTariff(tariff: TariffEntity): Response<Int> {
        return try {
            val hm = HashMap<String, Int>().apply {
                put("tariffId", tariffsCRUD.add(tariff).value)
            }

            Response.Success(HttpStatusCode.OK, hm)
        } catch (t: Throwable) {
            Response.Failure(HttpStatusCode.BadRequest, t.message.toString())
        }
    }
}