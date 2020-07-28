package billing

import PaymentEntity
import TariffChangeRequest
import db.tables.User
import db.tables.UserBalance
import db.tables.UsersBalancesCRUD
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object BalanceProcessor {
    private val userBalanceCRUD = UsersBalancesCRUD

    fun initBalance(id: Int) {
        transaction {
            UserBalance.new {
                user = User.findById(id) ?: throw NoSuchElementException("No such user")
                balance = 0
                lastOperationDate = DateTime()
            }
        }
    }

    fun addPaymentOntoBalance(paymentEntity: PaymentEntity) {
        transaction {
            val currentBalance = userBalanceCRUD.getByUserId(paymentEntity.contractNumber)

            currentBalance.balance += paymentEntity.totalAmount
            currentBalance.lastOperationDate = DateTime()
        }
    }

    fun withdrawMonthlyPaymentFromBalance() {
        transaction {

        }
    }

    fun withdrawTariffClosing(request: TariffChangeRequest) {
        transaction {
            TODO()
        }
    }
}