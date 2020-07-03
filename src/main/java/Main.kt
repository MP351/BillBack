
import api.webModule
import db.DbSettings
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.apache.log4j.BasicConfigurator
import watcher.Watcher

fun main() {
    BasicConfigurator.configure()
    DbSettings.db

    Watcher("/home/maxpayne/Share/SberTest").isRunning = true
    embeddedServer(Netty, port = 8080) {
        webModule()
    }.start(true)
}

