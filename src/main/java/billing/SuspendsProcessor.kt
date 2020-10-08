package billing

import billing.balance.BalanceProcessor
import days360
import db.tables.Suspend
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
            proceedSuspend(it, date)
        }
    }

    fun proceedSuspendForUser(user: User, date: DateTime) {
        if (user.isSuspended)
            return

        suspendsCRUD.getBegunSuspendForUser(user, date)?.let {
            proceedSuspend(it, date)
        }
    }

    private fun proceedSuspend(suspend: Suspend, date: DateTime) {
        val beginOfPeriod = getBeginningOfPeriod(suspend.user, date)
        val endOfPeriod = suspend.beginDate

        if (beginOfPeriod.days360(endOfPeriod) > 0)
            BalanceProcessor.scheduleWithdraws(suspend.user, beginOfPeriod, endOfPeriod)
        BalanceProcessor.removeScheduledWithdraws(suspend.user, date.getFirstDayOfNextMonth())
        suspend.user.isSuspended = true
        suspend.user.lastProcessedTime = date
    }

    private fun getBeginningOfPeriod(user: User, date: DateTime): DateTime {
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
                proceedResume(it, date)
            }
    }

    fun proceedResumeForUser(user: User, date: DateTime) {
        suspendsCRUD.getEndingSuspendForUser(user, date)?.let {
            proceedResume(it, date)
        }
    }

    private fun proceedResume(suspend: Suspend, date: DateTime) {
        BalanceProcessor.scheduleWithdraws(suspend.user, date, date.getFirstDayOfNextMonth())
        suspend.user.isSuspended = false
        suspend.isCompleted = true
        suspend.user.lastProcessedTime = date
    }
}