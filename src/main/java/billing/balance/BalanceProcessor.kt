package billing.balance

import db.tables.*
import org.jetbrains.exposed.sql.transactions.transaction

object BalanceProcessor {
    private val userBalanceCRUD = UsersBalancesCRUD
    private val userBalanceHistoryCRUD = UserBalancesHistoryCRUD()
    private val balanceOperationsCRUD = BalanceOperationCRUD()
    private val withdrawCRUD = WithdrawsCRUD

    fun initBalance(id: Int) {
        transaction {
            UserBalance.new(id) {
                balance = 0
            }
        }
    }

    fun proceedNewPayments(payments: List<Payment>) {
        payments.forEach {
            it.operation = addPaymentOntoBalance(
                    it.contractNumber.id.value,
                    it.totalAmount
            )
        }
    }

    private fun addPaymentOntoBalance(userId: Int, amount: Int): BalanceOperation {
        return transaction {
            val currentBalance = userBalanceCRUD.getByUserId(userId)
            currentBalance.balance += amount

            val operation = balanceOperationsCRUD.addAndGet(userId, 1)
            userBalanceHistoryCRUD.add(operation.id.value, userId, currentBalance.balance)

            operation
        }
    }

    fun withdrawMonthlyPaymentFromBalances() {
        transaction {
            val withdraws = withdrawCRUD.getUnprocessedWithdraws()

            withdraws.forEach {
                it.operation = withdrawPaymentFromBalance(
                        it.user.id.value,
                        it.amount
                )
            }
        }
    }

    private fun withdrawPaymentFromBalance(userId: Int, amount: Int): BalanceOperation {
        return transaction {
            val currentBalance = userBalanceCRUD.getByUserId(userId)
            currentBalance.balance -= amount

            val operation = balanceOperationsCRUD.addAndGet(userId, 1)
            userBalanceHistoryCRUD.add(operation.id.value, userId, currentBalance.balance)

            operation
        }
    }
}