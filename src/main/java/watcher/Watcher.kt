package watcher

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class Watcher(path: String) {
    private val dirFile = File(path)
    private val refreshPeriod = TimeUnit.MINUTES.toMillis(1)

    var isRunning by Delegates.observable(false) {
        _, _, newValue ->
        if (newValue)
            GlobalScope.launch {
                watch()
            }
    }

    private suspend fun watch() {
        if (!dirFile.exists() || !dirFile.isDirectory)
            throw IllegalArgumentException("Wrong dir path")

        while (isRunning) {
            dirFile.listFiles()?.forEach {
                if (!it.isDirectory) {
                    Parser().parse(it.readText(Charset.forName("Cp1251"))).forEach {
                        db.DbConnection.getInstance().insertPayment(it)
                    }

                    moveParsedFile(it)
                }
            }

            delay(refreshPeriod)
        }
    }

    private fun moveParsedFile(file: File) {
        val dir = File("${dirFile.path}/used")
        if (!dir.exists())
            dir.mkdir()

        file.copyTo(File("${dir.path}/${file.name}"))
        file.delete()
    }
}