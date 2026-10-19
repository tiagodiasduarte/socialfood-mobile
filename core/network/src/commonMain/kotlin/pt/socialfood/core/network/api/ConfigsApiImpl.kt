package pt.socialfood.core.network.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import pt.socialfood.core.model.Configs

internal class ConfigsApiImpl(private val client: HttpClient) : ConfigsApi {

    override suspend fun getConfigs(): Configs = client.get("configs").body()
}
