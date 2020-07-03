import db.SpeedLimit
import java.text.SimpleDateFormat

data class PaymentEntity(val dateTime: Long, val divisionNumber: String, val cashierNumber: String,
                         val operationCode: String, val contractNumber: Int, val name: String,
                         val totalAmount: Int, val incomeAmount: Int, val commissionAmount: Int)
data class PaymentEntityDB(val _id: Int, val dateTime: Long, val divisionNumber: String, val cashierNumber: String,
                           val operationCode: String, val contractNumber: Int, val name: String,
                           val totalAmount: Int, val incomeAmount: Int, val commissionAmount: Int)

//data class TariffEntity(val name: String, val price: Int)
//data class TariffEntityDB(val _id: Int, val name: String, val price: Int)

//data class UsersEntitySber(val name: String, val tariffId: Int, val active: Int)
//data class UsersEntity(val _id: Int, val name: String, val tariffId: Int, val active: Int)

// TODO: make suspension more informative. Type of suspension
data class SuspendEntity(val userId: Int, val beginDate: Long, val endDate: Long? = null)
data class SuspendEntityDB(val _id: Int, val userId: Int, val beginDate: Long, val endDate: Long? = null)

data class TariffHistoryEntity(val tariffId: Int, val userId: Int, val beginDate: Long, val endDate: Long? = null)
data class TariffHistoryEntityDB(val _id: Int, val tariff: TariffEntityDB, val user: UserEntity, val beginDate: Long, val endDate: Long? = null)

data class MonthlySummary(val _id: Int, val monthDate: Long, val user_id: Int, val sum: Int)
data class MonthlySummaryIn(val monthDate: Long, val user_id: Int, val sum: Int)

data class TariffEntity(val name: String, val price: Int, val speedLimits: SpeedLimit)
data class TariffEntityDB(val id: Int, val name: String, val price: Int, val speedLimits: SpeedLimit)
data class UserEntity(val contractNumber: Int, val firstName: String, val lastName: String, val fatherName: String, val tariffId: Int, val isActive: Boolean)

data class TariffChangeRequest(val contractNumber: Int, val tariffId: Int, val beginDate: Long)
//data class TariffChangeResponse()

data class SuspendRequest(val contractNumber: Int, val beginDate: Long)

//Class for api logins
data class LoginEntity(val login: String, val password: String)
data class LoginEntityDB(val _id: Int, val login: String, val password: String)
data class LoginEntityNoPassword(val _id: Int, val login: String)


// Classes with relations
data class UserWithTariffEntity(val contractNumber: Int, val firstName: String, val lastName: String, val fatherName: String, val tariffName: String, val isActive: Boolean)
data class UserWithTariffEntityNew(val contractNumber: Int, val firstName: String, val lastName: String, val fatherName: String, val tariff: TariffEntityDB, val isActive: Boolean)

object DateFormatter: SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")








