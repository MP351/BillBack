
import api.webModule
import billing.SuspendsProcessor
import billing.balance.OutOfDateBalanceActualizer
import billing.balance.PaymentsWatcher
import billing.balance.ProcessingWatcher
import db.DbSettings
import db.tables.SuspendsCRUD
import db.tables.User
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.log4j.BasicConfigurator
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import watcher.Watcher

fun main() {
    BasicConfigurator.configure()
    DbSettings.db

    initBalances()
    initWithdraws()
    OutOfDateBalanceActualizer().check()
    runBlocking {
        withContext(Dispatchers.IO) {
            PaymentsWatcher().isRunning = true
            ProcessingWatcher().isRunning = true
        }
    }
    Watcher("/home/maxpayne/Share/SberTest").isRunning = true
//    embeddedServer(Netty, port = 8080) {
//        webModule()
//    }.start(true)
    transaction {
//        print(SuspendsCRUD.getSuspendsForActiveUnsuspendedUsers(DateTime(2020, 1, 1, 0, 0)))
//        print(SuspendsProcessor.getBeginningOfPeriod(
//                User.findById(109)!!,
//                DateTime(2020, 6, 25, 20, 0)
//        ))
    }


}

