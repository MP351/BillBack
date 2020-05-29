package db

import org.junit.Assert.assertEquals
import org.junit.Test

internal class DbConnectionTest {

    @Test
    fun get360daysFeb2020() {
        val dbConnection = DbConnection.getInstance()
        assertEquals(30, dbConnection.get360days("2020-02-01", "2020-03-01"))
    }

    @Test
    fun get360days2020Year() {
        val dbConnection = DbConnection.getInstance()
        assertEquals(360, dbConnection.get360days("2020-01-01", "2021-01-01"))
    }

    @Test
    fun get360days2021Year() {
        val dbConnection = DbConnection.getInstance()
        assertEquals(360, dbConnection.get360days("2021-01-01", "2022-01-01"))
    }

    @Test
    fun get360days2020JanFeb() {
        val dbConnection = DbConnection.getInstance()
        assertEquals(30, dbConnection.get360days("2020-01-05", "2020-02-05"))
    }

    @Test
    fun get360days2020JanMay() {
        val dbConnection = DbConnection.getInstance()
        assertEquals(120, dbConnection.get360days("2020-01-10", "2020-05-10"))
    }

    @Test
    fun get360days2Years() {
        val dbConnection = DbConnection.getInstance()
        assertEquals(360*2, dbConnection.get360days("2019-01-01", "2021-01-01"))
    }

    @Test
    fun beginOfMonthValidDates() {
        val dbConnection = DbConnection.getInstance()
        assertEquals("2020-01-01", dbConnection.getBeginOfMonth("2020-01-05"))
        assertEquals("2020-02-01", dbConnection.getBeginOfMonth("2020-02-05"))
        assertEquals("2020-01-01", dbConnection.getBeginOfMonth("2020-01-31"))
    }

    @Test
    fun beginOfMonthInvalidDated() {
        val dbConnection = DbConnection.getInstance()
        assertEquals("2020-02-01", dbConnection.getBeginOfMonth("2020-01-32"))
    }
}