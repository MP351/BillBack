import org.joda.time.DateTime

fun DateTime.isToday(date: DateTime): Boolean {
    val today = DateTime()
    return date.year == today.year && date.monthOfYear == today.monthOfYear && date.dayOfMonth == today.dayOfMonth
}

fun DateTime.isToday(): Boolean {
    return isToday(this)
}

fun DateTime.getFirstDayOfNextMonth(date: DateTime): DateTime {
    return DateTime(date.year, date.monthOfYear, 1, 0, 0).plusMonths(1)
}

fun DateTime.getFirstDayOfNextMonth(): DateTime {
    return getFirstDayOfNextMonth(DateTime())
}


