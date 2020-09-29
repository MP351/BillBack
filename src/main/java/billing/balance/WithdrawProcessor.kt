package billing.balance

import db.tables.WithdrawsCRUD

object WithdrawProcessor {
    private val withdrawCRUD = WithdrawsCRUD

    fun proceedWithdraw() {

    }

    fun getUnprocessedWithdraws() {
        withdrawCRUD.getUnprocessedWithdraws()
    }
}