package pt.socialfood.core.network.di

import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module
import pt.socialfood.data.api.AuthApi
import pt.socialfood.data.api.AuthApiImpl
import pt.socialfood.data.api.AuthorsApi
import pt.socialfood.data.api.AuthorsApiImpl
import pt.socialfood.data.api.ConfigsApi
import pt.socialfood.data.api.ConfigsApiImpl
import pt.socialfood.data.api.FavouriteRestaurantsApi
import pt.socialfood.data.api.FavouriteRestaurantsApiImpl
import pt.socialfood.data.api.FavouritesGuidesApi
import pt.socialfood.data.api.FavouritesGuidesApiImpl
import pt.socialfood.data.api.GuidesApi
import pt.socialfood.data.api.GuidesApiImpl
import pt.socialfood.data.api.HomeApi
import pt.socialfood.data.api.HomeApiImpl
import pt.socialfood.data.api.PlacesApi
import pt.socialfood.data.api.PlacesApiImpl
import pt.socialfood.data.api.RestaurantApi
import pt.socialfood.data.api.RestaurantApiImpl
import pt.socialfood.data.api.RestaurantVisitStatusApi
import pt.socialfood.data.api.RestaurantVisitStatusApiImpl
import pt.socialfood.data.api.S3Api
import pt.socialfood.data.api.S3ApiImpl
import pt.socialfood.data.api.SearchApi
import pt.socialfood.data.api.SearchApiImpl
import pt.socialfood.data.api.UserApi
import pt.socialfood.data.api.UserApiImpl
import pt.socialfood.data.network.CoilHttpClient
import pt.socialfood.data.network.KtorHttpClient
import pt.socialfood.data.network.S3HttpClient

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
