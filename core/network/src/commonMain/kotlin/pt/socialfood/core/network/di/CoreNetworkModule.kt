package pt.socialfood.core.network.di

import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module
import pt.socialfood.core.network.CoilHttpClient
import pt.socialfood.core.network.KtorHttpClient
import pt.socialfood.core.network.S3HttpClient
import pt.socialfood.core.network.api.AuthApi
import pt.socialfood.core.network.api.AuthApiImpl
import pt.socialfood.core.network.api.AuthorsApi
import pt.socialfood.core.network.api.AuthorsApiImpl
import pt.socialfood.core.network.api.ConfigsApi
import pt.socialfood.core.network.api.ConfigsApiImpl
import pt.socialfood.core.network.api.FavouriteRestaurantsApi
import pt.socialfood.core.network.api.FavouriteRestaurantsApiImpl
import pt.socialfood.core.network.api.FavouritesGuidesApi
import pt.socialfood.core.network.api.FavouritesGuidesApiImpl
import pt.socialfood.core.network.api.GuidesApi
import pt.socialfood.core.network.api.GuidesApiImpl
import pt.socialfood.core.network.api.HomeApi
import pt.socialfood.core.network.api.HomeApiImpl
import pt.socialfood.core.network.api.PlacesApi
import pt.socialfood.core.network.api.PlacesApiImpl
import pt.socialfood.core.network.api.RestaurantApi
import pt.socialfood.core.network.api.RestaurantApiImpl
import pt.socialfood.core.network.api.RestaurantVisitStatusApi
import pt.socialfood.core.network.api.RestaurantVisitStatusApiImpl
import pt.socialfood.core.network.api.S3Api
import pt.socialfood.core.network.api.S3ApiImpl
import pt.socialfood.core.network.api.SearchApi
import pt.socialfood.core.network.api.SearchApiImpl
import pt.socialfood.core.network.api.UserApi
import pt.socialfood.core.network.api.UserApiImpl

val coreNetworkModule =
    module {
        includes(coreNetworkPlatformModule)
        single<AuthApi> { AuthApiImpl(get()) }
        single<AuthorsApi> { AuthorsApiImpl(get()) }
        single<ConfigsApi> { ConfigsApiImpl(get()) }
        single { CoilHttpClient() }
        single<FavouriteRestaurantsApi> { FavouriteRestaurantsApiImpl(get()) }
        single<FavouritesGuidesApi> { FavouritesGuidesApiImpl(get()) }
        single<GuidesApi> { GuidesApiImpl(get()) }
        single<HomeApi> { HomeApiImpl(get()) }
        single<HttpClient> { get<KtorHttpClient>().client }
        single { KtorHttpClient(get()) }
        single<PlacesApi> { PlacesApiImpl(get()) }
        single<RestaurantApi> { RestaurantApiImpl(get()) }
        single<S3Api> { S3ApiImpl(get<S3HttpClient>().client) }
        single { S3HttpClient() }
        single<SearchApi> { SearchApiImpl(get()) }
        single<UserApi> { UserApiImpl(get()) }
        single<RestaurantVisitStatusApi> { RestaurantVisitStatusApiImpl(get()) }
    }

internal expect val coreNetworkPlatformModule: Module
