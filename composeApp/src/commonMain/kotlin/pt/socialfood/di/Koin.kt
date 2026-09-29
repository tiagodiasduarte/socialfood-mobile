package pt.socialfood.di

import coil3.SingletonImageLoader
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
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
import pt.socialfood.data.local.AppDatabase
import pt.socialfood.data.network.CoilHttpClient
import pt.socialfood.data.network.KtorHttpClient
import pt.socialfood.data.network.S3HttpClient
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
import pt.socialfood.domain.repository.AuthRepository
import pt.socialfood.domain.repository.AuthorsRepository
import pt.socialfood.domain.repository.ConfigsRepository
import pt.socialfood.domain.repository.FavouriteRestaurantsRepository
import pt.socialfood.domain.repository.FavouritesGuidesRepository
import pt.socialfood.domain.repository.GuidesRepository
import pt.socialfood.domain.repository.HomeRepository
import pt.socialfood.domain.repository.LocalCacheRepository
import pt.socialfood.domain.repository.PhotosRepository
import pt.socialfood.domain.repository.PlacesRepository
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository
import pt.socialfood.domain.repository.RestaurantsRepository
import pt.socialfood.domain.repository.SearchRepository
import pt.socialfood.domain.repository.UsersRepository
import pt.socialfood.domain.session.SessionManager
import pt.socialfood.domain.usecase.configs.GetConfigsUseCase
import pt.socialfood.domain.usecase.configs.GetConfigsUseCaseImpl
import pt.socialfood.domain.usecase.guide.AddRestaurantGuideUseCase
import pt.socialfood.domain.usecase.guide.AddRestaurantGuideUseCaseImpl
import pt.socialfood.domain.usecase.guide.CreateGuideUseCase
import pt.socialfood.domain.usecase.guide.CreateGuideUseCaseImpl
import pt.socialfood.domain.usecase.guide.UpdateGuideUseCase
import pt.socialfood.domain.usecase.guide.UpdateGuideUseCaseImpl
import pt.socialfood.domain.usecase.login.LoginUseCase
import pt.socialfood.domain.usecase.login.LoginUseCaseImpl
import pt.socialfood.domain.usecase.login.LoginWithGoogleUseCase
import pt.socialfood.domain.usecase.login.LoginWithGoogleUseCaseImpl
import pt.socialfood.domain.usecase.login.LogoutUseCase
import pt.socialfood.domain.usecase.login.LogoutUseCaseImpl
import pt.socialfood.domain.usecase.login.RegisterUseCase
import pt.socialfood.domain.usecase.login.RegisterUseCaseImpl
import pt.socialfood.domain.usecase.login.ResendVerificationCodeUseCase
import pt.socialfood.domain.usecase.login.ResendVerificationCodeUseCaseImpl
import pt.socialfood.domain.usecase.login.RestartSignUpUseCase
import pt.socialfood.domain.usecase.login.RestartSignUpUseCaseImpl
import pt.socialfood.domain.usecase.login.ValidateCodeUseCase
import pt.socialfood.domain.usecase.login.ValidateCodeUseCaseImpl
import pt.socialfood.domain.usecase.photo.UploadPhotoUseCase
import pt.socialfood.domain.usecase.photo.UploadPhotoUseCaseImpl
import pt.socialfood.domain.usecase.search.SaveRecentSearchUseCase
import pt.socialfood.domain.usecase.search.SaveRecentSearchUseCaseImpl
import pt.socialfood.domain.usecase.search.SaveRecentSearchedPlaceUseCase
import pt.socialfood.domain.usecase.search.SaveRecentSearchedPlaceUseCaseImpl
import pt.socialfood.domain.usecase.theme.ObserveThemeModeUseCase
import pt.socialfood.domain.usecase.theme.ObserveThemeModeUseCaseImpl
import pt.socialfood.domain.usecase.theme.SetThemeModeUseCase
import pt.socialfood.domain.usecase.theme.SetThemeModeUseCaseImpl
import pt.socialfood.domain.usecase.user.ObserveUserUseCase
import pt.socialfood.domain.usecase.user.ObserveUserUseCaseImpl
import pt.socialfood.presentation.auth.di.authFeatureModule
import pt.socialfood.presentation.author.di.authorFeatureModule
import pt.socialfood.presentation.favourite.di.favouriteFeatureModule
import pt.socialfood.presentation.guide.di.guideFeatureModule
import pt.socialfood.presentation.home.di.homeFeatureModule
import pt.socialfood.presentation.map.di.mapFeatureModule
import pt.socialfood.presentation.profile.di.profileFeatureModule
import pt.socialfood.presentation.restaurant.di.restaurantFeatureModule
import pt.socialfood.presentation.search.di.searchFeatureModule
import pt.socialfood.presentation.settings.di.settingsFeatureModule
import pt.socialfood.presentation.sync.SyncViewModel
import pt.socialfood.presentation.ui.image.ImageCache

expect val platformModule: Module

val networkModule =
    module {
        single { AppImageLoaderFactory(get<CoilHttpClient>().client) }
        single<AuthApi> { AuthApiImpl(get()) }
        single<AuthorsApi> { AuthorsApiImpl(get()) }
        single<ConfigsApi> { ConfigsApiImpl(get()) }
        single { CoilHttpClient() }
        single<FavouriteRestaurantsApi> { FavouriteRestaurantsApiImpl(get()) }
        single<FavouritesGuidesApi> { FavouritesGuidesApiImpl(get()) }
        single<GuidesApi> { GuidesApiImpl(get()) }
        single<HomeApi> { HomeApiImpl(get()) }
        single<HttpClient> { get<KtorHttpClient>().client }
        single<ImageCache> { get<AppImageLoaderFactory>() }
        single { KtorHttpClient(get()) }
        single<PlacesApi> { PlacesApiImpl(get()) }
        single<RestaurantApi> { RestaurantApiImpl(get()) }
        single<S3Api> { S3ApiImpl(get<S3HttpClient>().client) }
        single { S3HttpClient() }
        single<SearchApi> { SearchApiImpl(get()) }
        single { SessionManager(get()) }
        single<UserApi> { UserApiImpl(get()) }
        single<RestaurantVisitStatusApi> { RestaurantVisitStatusApiImpl(get()) }
    }

val repositoryModule =
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

val useCaseModule =
    module {
        factory<AddRestaurantGuideUseCase> { AddRestaurantGuideUseCaseImpl(get(), get()) }
        factory<CreateGuideUseCase> { CreateGuideUseCaseImpl(get(), get()) }
        factory<GetConfigsUseCase> { GetConfigsUseCaseImpl(get()) }
        factory<LoginUseCase> { LoginUseCaseImpl(get(), get()) }
        factory<LoginWithGoogleUseCase> { LoginWithGoogleUseCaseImpl(get(), get()) }
        factory<LogoutUseCase> { LogoutUseCaseImpl(get(), get(), get(), get()) }
        factory<ObserveThemeModeUseCase> { ObserveThemeModeUseCaseImpl(get()) }
        factory<ObserveUserUseCase> { ObserveUserUseCaseImpl(get()) }
        factory<RegisterUseCase> { RegisterUseCaseImpl(get(), get()) }
        factory<ResendVerificationCodeUseCase> { ResendVerificationCodeUseCaseImpl(get()) }
        factory<RestartSignUpUseCase> { RestartSignUpUseCaseImpl(get()) }
        factory<SaveRecentSearchUseCase> { SaveRecentSearchUseCaseImpl(get()) }
        factory<SaveRecentSearchedPlaceUseCase> { SaveRecentSearchedPlaceUseCaseImpl(get()) }
        factory<SetThemeModeUseCase> { SetThemeModeUseCaseImpl(get()) }
        factory<UpdateGuideUseCase> { UpdateGuideUseCaseImpl(get(), get()) }
        factory<UploadPhotoUseCase> { UploadPhotoUseCaseImpl(get()) }
        factory<ValidateCodeUseCase> { ValidateCodeUseCaseImpl(get(), get(), get()) }
    }

val viewModelModule =
    module {
        factory { SyncViewModel(get(), get(), get(), get()) }
    }

fun initKoin(configuration: KoinAppDeclaration? = null) {
    val koinApplication =
        startKoin {
            includes(configuration)
            modules(
                module {
                    includes(
                        networkModule,
                        platformModule,
                        repositoryModule,
                        useCaseModule,
                        viewModelModule,
                        authFeatureModule,
                        authorFeatureModule,
                        favouriteFeatureModule,
                        guideFeatureModule,
                        homeFeatureModule,
                        mapFeatureModule,
                        profileFeatureModule,
                        restaurantFeatureModule,
                        searchFeatureModule,
                        settingsFeatureModule,
                    )
                },
            )
            printLogger(Level.DEBUG)
        }

    SingletonImageLoader.setSafe(koinApplication.koin.get<AppImageLoaderFactory>())
}
