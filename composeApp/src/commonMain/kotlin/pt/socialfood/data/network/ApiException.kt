package pt.socialfood.data.network

import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse

class ApiException(response: HttpResponse, val error: String, override val message: String) :
    ResponseException(response, message)
