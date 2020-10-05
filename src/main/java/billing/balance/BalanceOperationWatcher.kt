package billing.balance

import billing.SuspendsProcessor
import billing.UsersProcessor
import days360
import db.tables.SuspendsCRUD
import db.tables.User
import db.tables.Users
import db.tables.UsersCRUD
import isToday
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.castTo
import org.jetbrains.exposed.sql.jodatime.date
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
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
    private val suspendsProcessor = SuspendsProcessor

    var isRunning = Delegates.observable(false) {
        _, _, newValue ->
        if (newValue) {
            GlobalScope.launch {
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
    private val suspendsCRUD = SuspendsCRUD

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
            suspendsCRUD.getEndingSuspendForUser(user, processedDay.toDateTime())?.let {
                TODO()
            }

            processedDay.addDays(1)
        }
    }

    private fun isDatabaseOutOfDate(): Boolean {
        return usersCRUD.getActiveUsers().any {
            it.lastProcessedTime.days360(DateTime()) > 0
        }
    }
}