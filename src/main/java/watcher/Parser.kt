package watcher

import PaymentEntity
import org.joda.time.DateTime
import java.text.SimpleDateFormat

class Parser {
    fun parse(payments: String): List<PaymentEntity> {
        val listOfPayments = arrayListOf<PaymentEntity>()

        for (payment in payments.split(";\r\n=")[0].split(";\r\n")) {
            val tokens = payment.split(";")

            val sdf = SimpleDateFormat("dd-MM-yyyyHH-mm-ss")
            listOfPayments.add(PaymentEntity(sdf.parse("${tokens[0]}${tokens[1]}").time, tokens[2], tokens[3], tokens[4],
                    tokens[5].toInt(), tokens[6],
                    tokens[7].replace(",", "").toInt(),
                    tokens[8].replace(",", "").toInt(),
                    tokens[9].replace(",", "").toInt()))
        }

        return listOfPayments
    }
}

