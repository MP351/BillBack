import java.math.BigDecimal

data class PaymentEntitySber(val date: String, val time: String, val divisionNumber: String, val cashierNumber: String,
                             val operationCode: String, val contractNumber: Int, val name: String,
                             val totalAmount: String, val incomeAmount: String, val commissionAmount: String) {
    val totalAmountNum = BigDecimal(totalAmount.replace(',', '.'))
    val incomeAmountNum = BigDecimal(incomeAmount.replace(',', '.'))
    val commissionAmountNum= BigDecimal(commissionAmount.replace(',', '.'))
}
data class PaymentEntity(val _id: Int, val date: String, val time: String, val divisionNumber: String, val cashierNumber: String,
                          val operationCode: String, val contractNumber: Int, val name: String,
                          val totalAmount: Int, val incomeAmount: Int, val commissionAmount: Int) {
    val totalAmountNum = BigDecimal(totalAmount / 100.0)
    val incomeAmountNum = BigDecimal(incomeAmount / 100.0)
    val commissionAmountNum = BigDecimal(commissionAmount / 100.0)
}

data class TariffEntitySber(val name: String, val price: Int)
data class TariffEntity(val _id: Int, val name: String, val price: Int)

data class UsersEntitySber(val name: String, val tariff_id: Int, val active: Int)
data class UsersEntity(val _id: Int, val name: String, val tariff_id: Int, val active: Int)

data class SuspendEntitySber(val users_id: Int, val beginDate: String, val endDate: String = "")
data class SuspendEntity(val _id: Int, val users_id: Int, val beginDate: String, val endDate: String = "")

data class MonthlySummary(val _id: Int, val monthDate: String, val user_id: Int, val sum: Int) {
    val sumNum = BigDecimal(sum / 100.0)
}

//Class for api logins
data class LoginEntitySber(val login: String, val password: String)
data class LoginEntity(val _id: Int, val login: String, val password: String)


// Classes with relations
data class UserWithTariffEntity(val _id: Int, val name: String, val tariffName: String, val active: Int)







