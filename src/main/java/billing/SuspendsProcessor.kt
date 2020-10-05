package billing

import billing.balance.BalanceProcessor
import db.tables.SuspendsCRUD
import db.tables.User
import db.tables.UsersCRUD
import getFirstDayOfMonth
import getFirstDayOfNextMonth
import org.joda.time.DateTime

object SuspendsProcessor {
    private val usersCRUD = UsersCRUD
    private val suspendsCRUD = SuspendsCRUD


    fun proceedScheduledSuspends(date: DateTime = DateTime()) {
        val suspends = suspendsCRUD.getSuspendsForActiveUnsuspendedUsers(date)
        suspends.forEach {
            val beginOfPeriod = getBeginningOfPeriod(it.user, date)
            val endOfPeriod = it.beginDate

            BalanceProcessor.scheduleWithdraws(it.user, beginOfPeriod, endOfPeriod)
            BalanceProcessor.removeScheduledWithdraws(it.user, date.getFirstDayOfNextMonth())
            it.user.isSuspended = true
            it.user.lastProcessedTime = DateTime()
        }
    }

    fun getBeginningOfPeriod(user: User, date: DateTime): DateTime {
        val tariffActivationTime = usersCRUD.getTariffActivationDate(user, date)
        val firstDay = date.getFirstDayOfMonth()
        val closestSuspendEnd = suspendsCRUD.getLastSuspendEnding(user, date) ?: DateTime(Long.MIN_VALUE)

        var closest = firstDay

        if (tariffActivationTime.isAfter(closest))
            closest = tariffActivationTime
        if (closestSuspendEnd.isAfter(closest))
            closest = closestSuspendEnd

        return closest
    }

    fun proceedScheduledResumes(date: DateTime = DateTime()) {
            suspendsCRUD.getSuspendsScheduledForResume(date).forEach {
                BalanceProcessor.scheduleWithdraws(it.user, date, date.getFirstDayOfNextMonth())
                it.user.isSuspended = false
                it.isCompleted = true
                it.user.lastProcessedTime = DateTime()
            }
    }
}