package pt.socialfood.core

import pt.socialfood.domain.error.DataError

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val error: DataError) : Result<Nothing>()
}
