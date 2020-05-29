package db

import org.jetbrains.exposed.sql.Database

object DbSettings {
    val db by lazy {
        Database.connect("jdbc:postgresql://10.111.0.201:5432/viptec_billing",
                "org.postgresql.Driver", "sber_backend", "backendPassword")
    }
}