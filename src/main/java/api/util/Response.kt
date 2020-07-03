package api.util

import io.ktor.http.HttpStatusCode

sealed class Response<out T: Any> {
    data class Success<T: Any>(val code: HttpStatusCode, val data: HashMap<String, T>) : Response<T>()
    data class Failure(val code: HttpStatusCode, val message: String): Response<Nothing>()

    override fun toString(): String {
        return when(this) {
            is Success<*> -> "Success[data=$data]"
            is Failure -> "Failure[message=$message]"
        }
    }
}