package watcher

import PaymentEntitySber

class Parser {
    fun parse(payments: String): List<PaymentEntitySber> {
        val listOfPayments = arrayListOf<PaymentEntitySber>()

        for (payment in payments.split(";\r\n=")[0].split(";\r\n")) {
            val tokens = payment.split(";")

            listOfPayments.add(PaymentEntitySber(tokens[0], tokens[1], tokens[2], tokens[3], tokens[4],
                    tokens[5].toInt(), tokens[6], tokens[7], tokens[8], tokens[9]))
        }

        return listOfPayments
    }
}

