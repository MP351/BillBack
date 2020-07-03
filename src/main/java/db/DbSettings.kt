package db

import db.tables.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*

object DbSettings {
    val db by lazy {
        val dbProperties = Properties().apply {
            load(File("db.properties").inputStream())
        }
        Database.connect(
                dbProperties.getProperty("url"),
                "org.postgresql.Driver",
                dbProperties.getProperty("user"),
                dbProperties.getProperty("password")).apply {
            transaction {
                SchemaUtils.create(
                        Tariffs,
                        Users,
                        Payments,
                        InvalidPayments,
                        Suspends,
                        TariffsHistory,
                        MonthlySummary,
                        ApiUsers
                )
                SchemaUtils.createMissingTablesAndColumns(
                        Tariffs,
                        Users,
                        Payments,
                        InvalidPayments,
                        Suspends,
                        TariffsHistory,
                        MonthlySummary,
                        ApiUsers
                )
            }
        }
    }
}