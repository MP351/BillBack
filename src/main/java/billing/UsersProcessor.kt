package billing

import SuspendEntityDB
import SuspendRequest
import TariffChangeRequest
import TariffEntityDB
import TariffHistoryEntityDB
import UserEntity
import UserWithTariffEntityNew
import api.util.Response
import billing.balance.BalanceProcessor
import db.tables.*
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object UsersProcessor {
    private val usersCRUD = UsersCRUD
    private val tariffHistoryCRUD = TariffsHistoryCRUD
    private val suspendsCRUD = SuspendsCRUD
    private val balanceProcessor = BalanceProcessor

    fun newUser(id: Int?, firstName: String, lastName: String, fatherName: String,
                tariffId: Int, isSuspended: Boolean = true, isActive: Boolean = true, balance: Int? = null): User {
        val user = usersCRUD.addAndGet(id, firstName, lastName, fatherName, tariffId, isSuspended, isActive, DateTime())
        balanceProcessor.initBalance(user, balance)

        return user
    }

    fun getAllUsers(): List<User> {
        return transaction {
            User.all().toList()
        }
    }

    fun changeTariff(tariffChangeRequest: TariffChangeRequest): Response<UserEntity> {
        TODO()
    }

    fun suspend(suspendRequest: SuspendRequest): Response<UserEntity> {
        TODO()
    }

    fun getActiveUsers(): List<User> {
        return usersCRUD.getActiveUsers()
    }
}
