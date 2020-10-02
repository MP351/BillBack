import org.joda.time.DateTime
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class DateTimeUtilKtTest {

    @Test
    fun isToday1() {
        assertTrue(DateTime().isToday())
    }

    @Test
    fun isToday2() {
        val date = DateTime(2020, 1, 1, 0, 0)
        assertFalse(date.isToday())
    }

    @Test
    fun getFirstDayOfNextMonth1() {
        val date = DateTime(2020, 12, 31, 0, 0)
        val output = DateTime(date.year, date.monthOfYear, 1, 0, 0).plusMonths(1)
        assertEquals(output, date.getFirstDayOfNextMonth())
    }

    @Test
    fun days360_1() {
        val output = 30
        val begin = DateTime(2020, 2, 1, 0, 0)
        val end = DateTime(2020, 3, 1, 0, 0)

        assertEquals(output, begin.days360(end))
    }

    @Test
    fun days360_2() {
        val output = 28
        val begin = DateTime(2020, 2, 1, 0, 0)
        val end = DateTime(2020, 2, 29, 0, 0)

        assertEquals(output, begin.days360(end))
    }

    @Test
    fun days360_3() {
        val output = 60
        val begin = DateTime(2020, 1, 1, 0, 0)
        val end = DateTime(2020, 3, 1, 0, 0)

        assertEquals(output, begin.days360(end))
    }

    @Test
    fun days360_4() {
        val output = 360
        val begin = DateTime(2020, 1, 1, 0, 0)
        val end = DateTime(2021, 1, 1, 0, 0)

        assertEquals(output, begin.days360(end))
    }

    @Test
    fun days360_5() {
        val output = 30
        val begin = DateTime(2020, 1, 1, 0, 0)
        val end = DateTime(2020, 2, 1, 0, 0)

        assertEquals(output, begin.days360(end))
    }
}