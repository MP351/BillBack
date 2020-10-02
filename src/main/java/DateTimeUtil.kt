import org.joda.time.DateTime
import java.util.*


fun DateTime.isToday(): Boolean {
    val today = DateTime()
    return year == today.year && monthOfYear == today.monthOfYear && dayOfMonth == today.dayOfMonth
}

fun DateTime.getFirstDayOfNextMonth(date: DateTime): DateTime {
    return DateTime(date.year, date.monthOfYear, 1, 0, 0).plusMonths(1)
}

fun DateTime.getFirstDayOfNextMonth(): DateTime {
    return getFirstDayOfNextMonth(this)
}

fun DateTime.getFirstDayOfMonth(date: DateTime): DateTime {
    return DateTime(date.year, date.monthOfYear, 1, 0, 0)
}

fun DateTime.getFirstDayOfMonth(): DateTime {
    return getFirstDayOfMonth(this)
}

// Calculating US/NASD method
private fun getDays360(begin: DateTime, end: DateTime): Int {
    val beginYear = begin.year().get()
    val beginMonth = begin.monthOfYear().get()
    var beginDay = begin.dayOfMonth().get()

    val endYear = end.year().get()
    val endMonth = end.monthOfYear().get()
    var endDay = end.dayOfMonth().get()

    val calendar = Calendar.getInstance().apply {
        set(beginYear, beginMonth, beginDay)
    }

    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    if (beginMonth == 1 && endMonth == 1
            && beginDay == daysInMonth
            && endDay == daysInMonth) {
        endDay = 30
    }

    when(beginDay) {
        31, daysInMonth -> {
            beginDay = 30
            if (endDay == 31)
                endDay = 30
        }
    }

    return (endYear - beginYear) * 360 + (endMonth - beginMonth) * 30 + (endDay - beginDay)
}

fun DateTime.days360(date: DateTime): Int {
    return if (this.isBefore(date))
        getDays360(this, date)
    else
        getDays360(date, this)
}
