package pt.socialfood.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import pt.socialfood.domain.model.Configs

class ConfigsApiImpl(
    private val client: HttpClient
) : ConfigsApi {

    override suspend fun getConfigs(): Configs =
        client.get("configs").body()
}
