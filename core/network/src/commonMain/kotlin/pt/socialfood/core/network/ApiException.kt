package pt.socialfood.core.network

import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import pt.socialfood.core.common.error.ErrorCode

class ApiException(response: HttpResponse, val errorCode: ErrorCode, override val message: String) :
    ResponseException(response, message)
