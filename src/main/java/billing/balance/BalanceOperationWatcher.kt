package billing.balance

import billing.UsersProcessor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class PaymentsWatcher {
    private val refreshPeriod = TimeUnit.MINUTES.toMillis(10)
    private val paymentsProcessor = PaymentProcessor
    private val balanceProcessor = BalanceProcessor

    var isRunning = Delegates.observable(false) {
        _, _, newValue ->
        if (newValue) {
            GlobalScope.launch {
                watch()
            }
        }
    }

    private suspend fun watch() {
        val unprocessedPayments = paymentsProcessor.getUnprocessedPayments()
        balanceProcessor.processedNewPayments(unprocessedPayments)

        delay(refreshPeriod)
    }
}

class ProcessingWatcher {
    private val refreshPeriod = TimeUnit.MINUTES.toMillis(10)
    private val balanceProcessor = BalanceProcessor
    private val withdrawProcessor = WithdrawProcessor
    private val usersProcessor = UsersProcessor

    var isRunning = Delegates.observable(false) {
        _, _, newValue ->
        if (newValue) {
            GlobalScope.launch {
                watch()
            }
        }
    }

    private suspend fun watch() {
        // TODO()
        val activeUsers = usersProcessor.getActiveUsers()

        delay(refreshPeriod)
    }
}