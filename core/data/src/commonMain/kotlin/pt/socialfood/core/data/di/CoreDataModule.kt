package pt.socialfood.core.data.di

import org.koin.dsl.module
import pt.socialfood.core.data.paging.asAuthorCacheTransactionRunner
import pt.socialfood.core.data.paging.asFavouriteGuideCacheTransactionRunner
import pt.socialfood.core.data.paging.asFavouriteRestaurantCacheTransactionRunner
import pt.socialfood.core.data.paging.asGuideCacheTransactionRunner
import pt.socialfood.core.data.paging.asHomeCacheTransactionRunner
import pt.socialfood.core.data.paging.asRestaurantVisitStatusCacheTransactionRunner
import pt.socialfood.core.data.repository.AuthRepositoryImpl
import pt.socialfood.core.data.repository.AuthorsRepositoryImpl
import pt.socialfood.core.data.repository.ConfigsRepositoryImpl
import pt.socialfood.core.data.repository.FavouriteRestaurantsRepositoryImpl
import pt.socialfood.core.data.repository.FavouritesGuidesRepositoryImpl
import pt.socialfood.core.data.repository.GuidesRepositoryImpl
import pt.socialfood.core.data.repository.HomeRepositoryImpl
import pt.socialfood.core.data.repository.LocalCacheRepositoryImpl
import pt.socialfood.core.data.repository.PhotosRepositoryImpl
import pt.socialfood.core.data.repository.PlacesRepositoryImpl
import pt.socialfood.core.data.repository.RestaurantVisitStatusRepositoryImpl
import pt.socialfood.core.data.repository.RestaurantsRepositoryImpl
import pt.socialfood.core.data.repository.SearchRepositoryImpl
import pt.socialfood.core.data.repository.UsersRepositoryImpl
import pt.socialfood.core.database.AppDatabase
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
