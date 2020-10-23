package api

import LoginEntity
import LoginEntityDB
import LoginEntityNoPassword
import TariffEntityDB
import UserEntity
import UserWithTariffEntityNew
import api.util.Response
import billing.TariffsProcessor
import billing.UsersProcessor
import db.tables.ApiUsers
import org.jetbrains.exposed.sql.transactions.transaction

fun login(login: LoginEntity): Boolean {
    return when (LoginProcessor.getLoginByName(login)) {
        null -> false
        else -> true
    }
}

fun getLogins(): List<LoginEntityNoPassword> {
    return transaction {
        LoginProcessor.getLogins().map {
            LoginEntityNoPassword(it.id.value, it.login)
        }
    }
}

fun newApiUser(entity: LoginEntity): Int? {
    return transaction {
        LoginProcessor.addLogin(entity)
    }
}

fun getAllUsers(): List<UserEntity> {
    return transaction {
        UsersProcessor.getAllUsers().map {
            UserEntity(it.id.value, it.firstName, it.lastName, it.fatherName, it.tariff.id.value, it.tariffActivationDate.millis, it.isSuspended, it.isActive)
        }
    }
}

fun getAllUsersWithTariffs(): List<UserWithTariffEntityNew> {
    return transaction {
        UsersProcessor.getAllUsers().map {
            UserWithTariffEntityNew(it.id.value, it.firstName, it.lastName, it.fatherName,
                    TariffEntityDB(it.tariff.id.value, it.tariff.name, it.tariff.price, it.tariff.speedLimit),
                    it.isActive, it.tariffActivationDate.millis, it.isSuspended, it.lastProcessedTime.millis)
        }
    }
}

fun getTariffs(): List<TariffEntityDB> {
    return transaction {
        TariffsProcessor.getTariffs().map {
            TariffEntityDB(it.id.value, it.name, it.price, it.speedLimit)
        }
    }
}
