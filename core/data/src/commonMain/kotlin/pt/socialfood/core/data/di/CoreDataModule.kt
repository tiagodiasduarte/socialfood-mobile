package pt.socialfood.core.data.di

import org.koin.dsl.module
import pt.socialfood.core.domain.repository.AuthRepository
import pt.socialfood.core.domain.repository.AuthorsRepository
import pt.socialfood.core.domain.repository.ConfigsRepository
import pt.socialfood.core.domain.repository.FavouriteRestaurantsRepository
import pt.socialfood.core.domain.repository.FavouritesGuidesRepository
import pt.socialfood.core.domain.repository.GuidesRepository
import pt.socialfood.core.domain.repository.HomeRepository
import pt.socialfood.core.domain.repository.LocalCacheRepository
import pt.socialfood.core.domain.repository.PhotosRepository
import pt.socialfood.core.domain.repository.PlacesRepository
import pt.socialfood.core.domain.repository.RestaurantVisitStatusRepository
import pt.socialfood.core.domain.repository.RestaurantsRepository
import pt.socialfood.core.domain.repository.SearchRepository
import pt.socialfood.core.domain.repository.UsersRepository
import pt.socialfood.data.local.AppDatabase
import pt.socialfood.data.paging.asAuthorCacheTransactionRunner
import pt.socialfood.data.paging.asFavouriteGuideCacheTransactionRunner
import pt.socialfood.data.paging.asFavouriteRestaurantCacheTransactionRunner
import pt.socialfood.data.paging.asGuideCacheTransactionRunner
import pt.socialfood.data.paging.asHomeCacheTransactionRunner
import pt.socialfood.data.paging.asRestaurantVisitStatusCacheTransactionRunner
import pt.socialfood.data.repository.AuthRepositoryImpl
import pt.socialfood.data.repository.AuthorsRepositoryImpl
import pt.socialfood.data.repository.ConfigsRepositoryImpl
import pt.socialfood.data.repository.FavouriteRestaurantsRepositoryImpl
import pt.socialfood.data.repository.FavouritesGuidesRepositoryImpl
import pt.socialfood.data.repository.GuidesRepositoryImpl
import pt.socialfood.data.repository.HomeRepositoryImpl
import pt.socialfood.data.repository.LocalCacheRepositoryImpl
import pt.socialfood.data.repository.PhotosRepositoryImpl
import pt.socialfood.data.repository.PlacesRepositoryImpl
import pt.socialfood.data.repository.RestaurantVisitStatusRepositoryImpl
import pt.socialfood.data.repository.RestaurantsRepositoryImpl
import pt.socialfood.data.repository.SearchRepositoryImpl
import pt.socialfood.data.repository.UsersRepositoryImpl

val coreDataModule =
    module {
        single<AuthRepository> { AuthRepositoryImpl(get()) }
        single<AuthorsRepository> {
            AuthorsRepositoryImpl(
                authorsApi = get(),
                authorDao = get<AppDatabase>().authorDao(),
                authorRemoteKeyDao = get<AppDatabase>().authorRemoteKeyDao(),
                transactionRunner = get<AppDatabase>().asAuthorCacheTransactionRunner(),
            )
        }
        single<ConfigsRepository> { ConfigsRepositoryImpl(get()) }
        single<FavouriteRestaurantsRepository> {
            FavouriteRestaurantsRepositoryImpl(
                favouriteRestaurantsApi = get(),
                favouriteRestaurantDao = get<AppDatabase>().favouriteRestaurantDao(),
                favouriteRestaurantRemoteKeyDao = get<AppDatabase>().favouriteRestaurantRemoteKeyDao(),
                transactionRunner = get<AppDatabase>().asFavouriteRestaurantCacheTransactionRunner(),
                settingsRepository = get(),
            )
        }
        single<FavouritesGuidesRepository> {
            FavouritesGuidesRepositoryImpl(
                favouritesApi = get(),
                favouriteDao = get<AppDatabase>().favouriteDao(),
                favouriteGuideRemoteKeyDao = get<AppDatabase>().favouriteGuideRemoteKeyDao(),
                transactionRunner = get<AppDatabase>().asFavouriteGuideCacheTransactionRunner(),
                settingsRepository = get(),
            )
        }
        single<GuidesRepository> {
            GuidesRepositoryImpl(
                guideApi = get(),
                guideDao = get<AppDatabase>().guideDao(),
                guideRemoteKeyDao = get<AppDatabase>().guideRemoteKeyDao(),
                transactionRunner = get<AppDatabase>().asGuideCacheTransactionRunner(),
            )
        }
        single<HomeRepository> {
            HomeRepositoryImpl(
                homeApi = get(),
                homeDao = get<AppDatabase>().homeDao(),
                transactionRunner = get<AppDatabase>().asHomeCacheTransactionRunner(),
            )
        }
        single<LocalCacheRepository> { LocalCacheRepositoryImpl(get()) }
        single<PhotosRepository> { PhotosRepositoryImpl(get()) }
        single<PlacesRepository> { PlacesRepositoryImpl(get()) }
        single<RestaurantsRepository> { RestaurantsRepositoryImpl(get()) }
        single<SearchRepository> { SearchRepositoryImpl(get()) }
        single<UsersRepository> { UsersRepositoryImpl(get()) }
        single<RestaurantVisitStatusRepository> {
            RestaurantVisitStatusRepositoryImpl(
                restaurantVisitStatusApi = get(),
                restaurantVisitStatusDao = get<AppDatabase>().restaurantVisitStatusDao(),
                restaurantVisitStatusRemoteKeyDao = get<AppDatabase>().restaurantVisitStatusRemoteKeyDao(),
                transactionRunner = get<AppDatabase>().asRestaurantVisitStatusCacheTransactionRunner(),
                settingsRepository = get(),
            )
        }
    }
