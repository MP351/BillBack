package billing.balance

import billing.SuspendsProcessor
import days360
import db.tables.User
import db.tables.UsersCRUD
import isToday
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class PaymentsWatcher {
    private val refreshPeriod = TimeUnit.MINUTES.toMillis(10)
    private val paymentsProcessor = PaymentProcessor
    private val balanceProcessor = BalanceProcessor

    var isRunning: Boolean by Delegates.observable(false) {
        _, _, newValue ->
        if (newValue) {
            runBlocking {
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
    private val suspendsProcessor = SuspendsProcessor

    var isRunning: Boolean by Delegates.observable(false) {
        _, _, newValue ->
        if (newValue) {
            runBlocking {
                watch()
            }
        }
    }

    private suspend fun watch() {
        suspendsProcessor.proceedScheduledSuspends()
        suspendsProcessor.proceedScheduledResumes()
        balanceProcessor.proceedScheduledWithdraws()

        delay(refreshPeriod)
    }
}

class OutOfDateBalanceActualizer {
    private val usersCRUD = UsersCRUD
    private val suspendsProcessor = SuspendsProcessor
    private val balanceProcessor = BalanceProcessor

    fun check() {
        if (!isDatabaseOutOfDate())
            return

        usersCRUD.getActiveUsers().filter {
            it.lastProcessedTime.days360(DateTime()) > 0
        }.forEach {
            actualizeUser(it)
        }
    }

    private fun actualizeUser(user: User) {
        val processedDay = MutableDateTime(user.lastProcessedTime)
        while (!processedDay.isToday()) {
            val date = processedDay.toDateTime()
            suspendsProcessor.proceedSuspendForUser(user, date)
            suspendsProcessor.proceedResumeForUser(user, date)
            balanceProcessor.proceedWithdraw(user, date)

            processedDay.addDays(1)
        }
    }

    private fun isDatabaseOutOfDate(): Boolean {
        return usersCRUD.getActiveUsers().any {
            it.lastProcessedTime.days360(DateTime()) > 0
        }
    }
}