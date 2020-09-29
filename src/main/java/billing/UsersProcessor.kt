package billing

import SuspendEntityDB
import SuspendRequest
import TariffChangeRequest
import TariffEntityDB
import TariffHistoryEntityDB
import UserEntity
import UserWithTariffEntityNew
import api.util.Response
import db.tables.*
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.IllegalArgumentException

object UsersProcessor {
    private val usersCRUD = UsersCRUD
    private val tariffHistoryCRUD = TariffsHistoryCRUD
    private val suspendsCRUD = SuspendsCRUD

    fun addUser(userEntity: UserEntity): Response<Int> {
        return try {
            val hm = HashMap<String, Int>().apply {
                put("userId", usersCRUD.add(userEntity).value)
            }
            Response.Success(HttpStatusCode.OK, hm)
        } catch (t: Throwable) {
            Response.Failure(HttpStatusCode.BadRequest, t.message.toString())
        }
    }

    fun getUsers(): Response<List<UserEntity>> {
        return try {
            val users = usersCRUD.getAll()
            val hm = HashMap<String, List<UserEntity>>().apply {
                transaction {
                    put("usersWithTariff", users.map {
                        UserEntity(
                                it.id.value,
                                it.firstName,
                                it.lastName,
                                it.fatherName,
                                it.tariffId.id.value,
                                it.tariffActivationDate.millis,
                                it.isSuspended,
                                it.isActive
                        )
                    })
                }
            }
            Response.Success(HttpStatusCode.OK, hm)
        } catch (t: Throwable) {
            Response.Failure(HttpStatusCode.BadRequest, t.message.toString())
        }
    }

    fun getUsersWithTariffs(): Response<List<UserWithTariffEntityNew>> {
        return try {
            val users = usersCRUD.getAll()
            val hm = HashMap<String, List<UserWithTariffEntityNew>>().apply {
                transaction {
                    put("usersWithTariff", users.map {
                        UserWithTariffEntityNew(
                                it.id.value,
                                it.firstName,
                                it.lastName,
                                it.fatherName,
                                TariffEntityDB(
                                        it.tariffId.id.value,
                                        it.tariffId.name,
                                        it.tariffId.price,
                                        it.tariffId.speedLimit
                                ),
                                it.isActive
                        )
                    })
                }
            }
            Response.Success(HttpStatusCode.OK, hm)
        } catch (t: Throwable) {
            Response.Failure(HttpStatusCode.BadRequest, t.message.toString())
        }
    }

    fun getUserByContractNumber(id: Int): Response<UserEntity> {
        return try {
            val user = usersCRUD.getById(id)
            val hm = HashMap<String, UserEntity>().apply {
                transaction {
                    put("user", UserEntity(
                            user.id.value,
                            user.firstName,
                            user.lastName,
                            user.fatherName,
                            user.tariffId.id.value,
                            user.tariffActivationDate.millis,
                            user.isSuspended,
                            user.isActive))
                }
            }
            Response.Success(HttpStatusCode.OK, hm)
        } catch (t: Throwable) {
            Response.Failure(HttpStatusCode.BadRequest, t.message.toString())
        }
    }

    fun changeTariff(tariffChangeRequest: TariffChangeRequest): Response<UserEntity> {
        TODO()
    }

    fun suspend(suspendRequest: SuspendRequest): Response<UserEntity> {
        TODO()
    }

    fun getTariffsHistory(contractNumber: Int): Response<List<TariffHistoryEntityDB>> {
        return try {
            val history = tariffHistoryCRUD.getTariffHistoryForUser(contractNumber)
            val hm = HashMap<String, List<TariffHistoryEntityDB>>().apply {
                put("tariffHistory", history.map {
                    transaction {
                        TariffHistoryEntityDB(
                                it.id.value,
                                TariffEntityDB(
                                        it.tariff.id.value,
                                        it.tariff.name,
                                        it.tariff.price,
                                        it.tariff.speedLimit
                                ),
                                UserEntity(
                                        it.user.id.value,
                                        it.user.firstName,
                                        it.user.lastName,
                                        it.user.fatherName,
                                        it.user.tariffId.id.value,
                                        it.user.tariffActivationDate.millis,
                                        it.user.isSuspended,
                                        it.user.isActive
                                ),
                                it.beginDate.millis,
                                it.endDate.millis
                        )
                    }
                })
            }
            Response.Success(HttpStatusCode.OK, hm)
        } catch (t: Throwable) {
            Response.Failure(HttpStatusCode.BadRequest, t.message.toString())
        }
    }

    fun getCurrentSuspension(contractNumber: Int): Response<SuspendEntityDB> {
        return try {
            val suspension = suspendsCRUD.getCurrentSuspension(contractNumber)
            val hm = HashMap<String, SuspendEntityDB>()
            suspension?.let {
                hm.put("suspend", SuspendEntityDB(
                        suspension.id.value,
                        suspension.user.id.value,
                        suspension.beginDate.millis,
                        suspension.endDate?.millis
                ))
            }
            Response.Success(HttpStatusCode.OK, hm)
        } catch (t: Throwable) {
            Response.Failure(HttpStatusCode.BadRequest, t.message.toString())
        }
    }

    fun getSuspends(contractNumber: Int): Response<List<SuspendEntityDB>> {
        return try {
            val suspensions = suspendsCRUD.getSuspendsForContract(contractNumber)
            val hm = HashMap<String, List<SuspendEntityDB>>().apply {
                put("suspend", suspensions.map {
                    SuspendEntityDB(
                            it.id.value,
                            it.user.id.value,
                            it.beginDate.millis,
                            it.endDate?.millis
                    )
                })
            }
            Response.Success(HttpStatusCode.OK, hm)
        } catch (t: Throwable) {
            Response.Failure(HttpStatusCode.BadRequest, t.message.toString())
        }
    }
}
