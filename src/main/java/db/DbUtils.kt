package db

import LoginEntity
import LoginEntitySber
import PaymentEntitySber
import SuspendEntity
import SuspendEntitySber
import TariffEntity
import TariffEntitySber
import UsersEntity
import UsersEntitySber
import java.math.BigDecimal
import java.sql.DriverManager
import java.text.SimpleDateFormat

class DbConnection private constructor() {
    private val dbVersion = 1
    private val url = "jdbc:sqlite:payments_$dbVersion.db"
    private val dateFormatter = SimpleDateFormat("YYYY-MM-DD HH:MM:SS.SSS")


    fun initDb() {
        DriverManager.getConnection(url).use {
            it.createStatement().execute(PaymentsContract.SQL_CREATE_ENTRIES)
            it.createStatement().execute(TariffsContract.SQL_CREATE_ENTRIES)
            it.createStatement().execute(UsersContract.SQL_CREATE_ENTRIES)
            it.createStatement().execute(SuspendsContract.SQL_CREATE_ENTRIES)
            it.createStatement().execute(LoginContract.SQL_CREATE_ENTRIES)
        }
    }

    fun insertPayment(paymentEntity: PaymentEntitySber) {
        DriverManager.getConnection(url).use {
            it.prepareStatement(PaymentsContract.SQL_DATA_INSERT).apply {
                with(paymentEntity) {
                    setString(1, date)
                    setString(2, time)
                    setString(3, divisionNumber)
                    setString(4, cashierNumber)
                    setString(5, operationCode)
                    setInt(6, contractNumber)
                    setString(7, name)
                    setInt(8, (totalAmountNum.multiply(BigDecimal(100))).toInt())
                    setInt(9, (incomeAmountNum.multiply(BigDecimal(100))).toInt())
                    setInt(10, (commissionAmountNum.multiply(BigDecimal(100))).toInt())
                }
            }
        }
    }

    fun insertTariff(tariffEntitySber: TariffEntitySber) {
        DriverManager.getConnection(url).use {
            it.prepareStatement(TariffsContract.SQL_DATA_INSERT).apply {
                with(tariffEntitySber) {
                    setString(1, name)
                    setInt(2, price)
                }
            }
        }
    }

    fun insertUser(usersEntitySber: UsersEntitySber) {
        DriverManager.getConnection(url).use {
            it.prepareStatement(UsersContract.SQL_DATA_INSERT).apply {
                with(usersEntitySber) {
                    setString(1, name)
                    setInt(2, tariff_id)
                    setInt(3, active)
                }
            }
        }
    }

    fun insertSuspending(suspendEntitySber: SuspendEntitySber) {
        DriverManager.getConnection(url).use {
            it.prepareStatement(SuspendsContract.SQL_DATA_INSERT).apply {
                with(suspendEntitySber) {
                    setInt(1, users_id)
                    setString(2, beginDate)
                    setString(3, endDate)
                }
            }
        }
    }

    fun insertLogin(loginEntitySber: LoginEntitySber) {
        DriverManager.getConnection(url).use {
            it.prepareStatement(LoginContract.SQL_DATE_INSERT).apply {
                with(loginEntitySber) {
                    setString(1, login)
                    setString(2, password)
                }
            }
        }
    }

    fun getLogins(): List<LoginEntity> {
        DriverManager.getConnection(url).use {
            return with(it.createStatement().executeQuery("SELECT * FROM ${LoginContract.TABLE_NAME}")) {
                val list = ArrayList<LoginEntity>()
                while (next()) {
                    list.add(LoginEntity(getInt("_ID"), getString(LoginContract.COLUMN_NAME_LOGIN), getString(LoginContract.COLUMN_NAME_PASSWORD)))
                }
                list
            }
        }
    }

    fun getLoginsMap(): HashMap<String, LoginEntity> {
        DriverManager.getConnection(url).use {
            return with(it.createStatement().executeQuery("SELECT * FROM ${LoginContract.TABLE_NAME}")) {
                val list = HashMap<String, LoginEntity>()
                while (next()) {
                    list.put(getString(LoginContract.COLUMN_NAME_LOGIN),
                            LoginEntity(getInt("_ID"), getString(LoginContract.COLUMN_NAME_LOGIN),
                                    getString(LoginContract.COLUMN_NAME_PASSWORD)))
                }
                list
            }
        }
    }

    fun getUsers(): ArrayList<UsersEntity> {
        DriverManager.getConnection(url).use {
            return with(it.createStatement().executeQuery("SELECT * FROM ${UsersContract.TABLE_NAME}")) {
                val list = ArrayList<UsersEntity>()
                while (next()) {
                    list.add(UsersEntity(getInt("_ID"), getString(UsersContract.COLUMN_NAME_NAME), getInt(UsersContract.COLUMN_NAME_TARIFF_ID), getInt(UsersContract.COLUMN_NAME_ACTIVE)))
                }
                list
            }
        }
    }

    fun getTariffs(): ArrayList<TariffEntity> {
        DriverManager.getConnection(url).use {
            return with(it.createStatement().executeQuery("SELECT * FROM ${TariffsContract.TABLE_NAME}")) {
                val list = ArrayList<TariffEntity>()
                while (next()) {
                    list.add(TariffEntity(getInt("_ID"), getString(TariffsContract.COLUMN_NAME_NAME), getInt(TariffsContract.COLUMN_NAME_PRICE)))
                }
                list
            }
        }
    }

    fun getSuspends(): ArrayList<SuspendEntity> {
        DriverManager.getConnection(url).use {
            return with(it.createStatement().executeQuery("SELECT  FROM ${SuspendsContract.TABLE_NAME}")) {
                val list = ArrayList<SuspendEntity>()
                while (next()) {
                    list.add(SuspendEntity(getInt("_ID"), getInt(SuspendsContract.COLUMN_NAME_USER_ID), getString(SuspendsContract.COLUMN_NAME_BEGIN_DATE), getString(SuspendsContract.COLUMN_NAME_END_DATE)))
                }
                list
            }
        }
    }

    companion object {
        private val instance: DbConnection = DbConnection()

        fun getInstance(): DbConnection {
            return instance
        }
    }
}

object PaymentsContract {
    const val TABLE_NAME = "payments"
    const val COLUMN_NAME_DATE = "date"
    const val COLUMN_NAME_TIME = "time"
    const val COLUMN_NAME_DIV_NUM = "division_number"
    const val COLUMN_NAME_CASHIER_NUM = "cashier_number"
    const val COLUMN_NAME_OPCODE = "opcode"
    const val COLUMN_NAME_CONTRACT_NUMBER = "contract_number"
    const val COLUMN_NAME_NAME = "name"
    const val COLUMN_NAME_TOTAL_AMOUNT = "total_amount"
    const val COLUMN_NAME_INCOME_AMOUNT = "income_amount"
    const val COLUMN_NAME_COMMISSION_AMOUNT = "commission_amount"

    const val SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS $TABLE_NAME(" +
            "_ID INTEGER PRIMARY KEY NOT NULL," +
            "$COLUMN_NAME_DATE TEXT NOT NULL," +
            "$COLUMN_NAME_TIME TEXT NOT NULL," +
            "$COLUMN_NAME_DIV_NUM TEXT NOT NULL," +
            "$COLUMN_NAME_CASHIER_NUM TEXT NOT NULL," +
            "$COLUMN_NAME_OPCODE TEXT NOT NULL," +
            "$COLUMN_NAME_CONTRACT_NUMBER INTEGER NOT NULL," +
            "$COLUMN_NAME_NAME TEXT NOT NULL," +
            "$COLUMN_NAME_TOTAL_AMOUNT INTEGER NOT NULL," +
            "$COLUMN_NAME_INCOME_AMOUNT INTEGER NOT NULL," +
            "$COLUMN_NAME_COMMISSION_AMOUNT INTEGER NOT NULL)"

    // Init data for 1 march 2020
    const val SQL_DATA_INSERT = "INSERT INTO $TABLE_NAME($COLUMN_NAME_DATE, $COLUMN_NAME_TIME, $COLUMN_NAME_DIV_NUM," +
            "$COLUMN_NAME_CASHIER_NUM, $COLUMN_NAME_OPCODE, $COLUMN_NAME_CONTRACT_NUMBER, $COLUMN_NAME_NAME, " +
            "$COLUMN_NAME_TOTAL_AMOUNT, $COLUMN_NAME_INCOME_AMOUNT, $COLUMN_NAME_COMMISSION_AMOUNT) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
}

object TariffsContract {
    const val TABLE_NAME = "tariffs"
    const val COLUMN_NAME_NAME = "name"
    const val COLUMN_NAME_PRICE = "price"

    const val SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS $TABLE_NAME(" +
            "_ID INTEGER PRIMARY KEY NOT NULL," +
            "$COLUMN_NAME_NAME TEXT NOT NULL," +
            "$COLUMN_NAME_PRICE INTEGER NOT NULL)"

    const val SQL_DATA_INSERT = "INSERT INTO $TABLE_NAME($COLUMN_NAME_NAME, $COLUMN_NAME_PRICE) VALUE(?, ?)"
}

object UsersContract {
    const val TABLE_NAME = "users"
    const val COLUMN_NAME_NAME = "name"
    const val COLUMN_NAME_TARIFF_ID = "tariff_id"
    const val COLUMN_NAME_ACTIVE = "active"

    const val SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS $TABLE_NAME(" +
            "_ID INTEGER PRIMARY KEY NOT NULL," +
            "$COLUMN_NAME_NAME TEXT NOT NULL," +
            "$COLUMN_NAME_TARIFF_ID INTEGER NOT NULL," +
            "$COLUMN_NAME_ACTIVE INTEGER NOT NULL)"

    const val SQL_DATA_INSERT = "INSERT INTO $TABLE_NAME(_ID, $COLUMN_NAME_NAME, $COLUMN_NAME_TARIFF_ID, $COLUMN_NAME_ACTIVE)" +
            "VALUES(?, ?, ?, ?)"
}

object SuspendsContract {
    const val TABLE_NAME = "suspends"
    const val COLUMN_NAME_USER_ID = "user_id"
    const val COLUMN_NAME_BEGIN_DATE = "begin_date"
    const val COLUMN_NAME_END_DATE = "end_date"

    const val SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS $TABLE_NAME(" +
            "_ID INTEGER_PRIMARY KEY NOT NULL," +
            "$COLUMN_NAME_USER_ID INTEGER NOT NULL," +
            "$COLUMN_NAME_BEGIN_DATE TEXT NOT NULL," +
            "$COLUMN_NAME_END_DATE TEXT)"

    const val SQL_DATA_INSERT = "INSERT INTO $TABLE_NAME($COLUMN_NAME_USER_ID, $COLUMN_NAME_BEGIN_DATE) VALUES(?, ?)"
}

object LoginContract {
    const val TABLE_NAME = "au_users"
    const val COLUMN_NAME_LOGIN = "login"
    const val COLUMN_NAME_PASSWORD = "password"

    const val SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS $TABLE_NAME(" +
            "_ID INTEGER PRIMARY KEY NOT NULL," +
            "$COLUMN_NAME_LOGIN TEXT NOT NULL," +
            "$COLUMN_NAME_PASSWORD TEXT NOT NULL)"

    const val SQL_DATE_INSERT = "INSET INTO $TABLE_NAME($COLUMN_NAME_LOGIN, $COLUMN_NAME_PASSWORD) VALUES(?, ?)"
}