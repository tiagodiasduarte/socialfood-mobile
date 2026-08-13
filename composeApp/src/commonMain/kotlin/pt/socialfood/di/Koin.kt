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
import pt.socialfood.data.api.S3Api
import pt.socialfood.data.api.S3ApiImpl
import pt.socialfood.data.api.SearchApi
import pt.socialfood.data.api.SearchApiImpl
import pt.socialfood.data.api.UserApi
import pt.socialfood.data.api.UserApiImpl
import pt.socialfood.data.local.AppDatabase
import pt.socialfood.data.network.ImageHttpClient
import pt.socialfood.data.network.KtorHttpClient
import pt.socialfood.data.network.S3HttpClient
import pt.socialfood.data.network.SessionManager
import pt.socialfood.data.paging.asAuthorCacheTransactionRunner
import pt.socialfood.data.paging.asGuideCacheTransactionRunner
import pt.socialfood.data.paging.asHomeCacheTransactionRunner
import pt.socialfood.data.repository.AuthRepositoryImpl
import pt.socialfood.data.repository.AuthorsRepositoryImpl
import pt.socialfood.data.repository.ConfigsRepositoryImpl
import pt.socialfood.data.repository.FavouriteRestaurantsRepositoryImpl
import pt.socialfood.data.repository.FavouritesGuidesRepositoryImpl
import pt.socialfood.data.repository.GuidesRepositoryImpl
import pt.socialfood.data.repository.HomeRepositoryImpl
import pt.socialfood.data.repository.PhotosRepositoryImpl
import pt.socialfood.data.repository.PlacesRepositoryImpl
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
import pt.socialfood.domain.repository.PhotosRepository
import pt.socialfood.domain.repository.PlacesRepository
import pt.socialfood.domain.repository.RestaurantsRepository
import pt.socialfood.domain.repository.SearchRepository
import pt.socialfood.domain.repository.UsersRepository
import pt.socialfood.domain.usecase.SearchPlacesUseCase
import pt.socialfood.domain.usecase.SearchPlacesUseCaseImpl
import pt.socialfood.domain.usecase.author.FindAuthorsUseCase
import pt.socialfood.domain.usecase.author.FindAuthorsUseCaseImpl
import pt.socialfood.domain.usecase.author.GetAuthorByIdUseCase
import pt.socialfood.domain.usecase.author.GetAuthorByIdUseCaseImpl
import pt.socialfood.domain.usecase.author.GetAuthorsPagingUseCase
import pt.socialfood.domain.usecase.author.GetAuthorsPagingUseCaseImpl
import pt.socialfood.domain.usecase.author.GetAuthorsUseCase
import pt.socialfood.domain.usecase.author.GetAuthorsUseCaseImpl
import pt.socialfood.domain.usecase.configs.GetConfigsUseCase
import pt.socialfood.domain.usecase.configs.GetConfigsUseCaseImpl
import pt.socialfood.domain.usecase.favourite.SyncFavouriteRestaurantsUseCase
import pt.socialfood.domain.usecase.favourite.SyncFavouriteRestaurantsUseCaseImpl
import pt.socialfood.domain.usecase.favourite.SyncFavouritesUseCase
import pt.socialfood.domain.usecase.favourite.SyncFavouritesUseCaseImpl
import pt.socialfood.domain.usecase.favourite.guide.GetFavouriteGuidesUseCase
import pt.socialfood.domain.usecase.favourite.guide.GetFavouriteGuidesUseCaseImpl
import pt.socialfood.domain.usecase.favourite.guide.IsGuideFavouriteUseCase
import pt.socialfood.domain.usecase.favourite.guide.IsGuideFavouriteUseCaseImpl
import pt.socialfood.domain.usecase.favourite.guide.MarkGuideFavouriteUseCase
import pt.socialfood.domain.usecase.favourite.guide.MarkGuideFavouriteUseCaseImpl
import pt.socialfood.domain.usecase.favourite.guide.ObserveFavouriteGuideIdsUseCase
import pt.socialfood.domain.usecase.favourite.guide.ObserveFavouriteGuideIdsUseCaseImpl
import pt.socialfood.domain.usecase.favourite.guide.UnmarkGuideFavouriteUseCase
import pt.socialfood.domain.usecase.favourite.guide.UnmarkGuideFavouriteUseCaseImpl
import pt.socialfood.domain.usecase.favourite.restaurant.GetFavouriteRestaurantsUseCase
import pt.socialfood.domain.usecase.favourite.restaurant.GetFavouriteRestaurantsUseCaseImpl
import pt.socialfood.domain.usecase.favourite.restaurant.IsRestaurantFavouriteUseCase
import pt.socialfood.domain.usecase.favourite.restaurant.IsRestaurantFavouriteUseCaseImpl
import pt.socialfood.domain.usecase.favourite.restaurant.MarkRestaurantFavouriteUseCase
import pt.socialfood.domain.usecase.favourite.restaurant.MarkRestaurantFavouriteUseCaseImpl
import pt.socialfood.domain.usecase.favourite.restaurant.UnmarkRestaurantFavouriteUseCase
import pt.socialfood.domain.usecase.favourite.restaurant.UnmarkRestaurantFavouriteUseCaseImpl
import pt.socialfood.domain.usecase.guide.AddRestaurantGuideUseCase
import pt.socialfood.domain.usecase.guide.AddRestaurantGuideUseCaseImpl
import pt.socialfood.domain.usecase.guide.CreateGuideUseCase
import pt.socialfood.domain.usecase.guide.CreateGuideUseCaseImpl
import pt.socialfood.domain.usecase.guide.DeleteGuideUseCase
import pt.socialfood.domain.usecase.guide.DeleteGuideUseCaseImpl
import pt.socialfood.domain.usecase.guide.FindGuidesUseCase
import pt.socialfood.domain.usecase.guide.FindGuidesUseCaseImpl
import pt.socialfood.domain.usecase.guide.GetGuideByIdUseCase
import pt.socialfood.domain.usecase.guide.GetGuideByIdUseCaseImpl
import pt.socialfood.domain.usecase.guide.GetGuidesPagingUseCase
import pt.socialfood.domain.usecase.guide.GetGuidesPagingUseCaseImpl
import pt.socialfood.domain.usecase.guide.GetGuidesUseCase
import pt.socialfood.domain.usecase.guide.GetGuidesUseCaseImpl
import pt.socialfood.domain.usecase.guide.UpdateGuideUseCase
import pt.socialfood.domain.usecase.guide.UpdateGuideUseCaseImpl
import pt.socialfood.domain.usecase.home.AddHomeSectionItemUseCase
import pt.socialfood.domain.usecase.home.AddHomeSectionItemUseCaseImpl
import pt.socialfood.domain.usecase.home.CreateHomeSectionUseCase
import pt.socialfood.domain.usecase.home.CreateHomeSectionUseCaseImpl
import pt.socialfood.domain.usecase.home.DeleteHomeSectionUseCase
import pt.socialfood.domain.usecase.home.DeleteHomeSectionUseCaseImpl
import pt.socialfood.domain.usecase.home.GetHomeSectionByIdUseCase
import pt.socialfood.domain.usecase.home.GetHomeSectionByIdUseCaseImpl
import pt.socialfood.domain.usecase.home.GetHomeSectionsUseCase
import pt.socialfood.domain.usecase.home.GetHomeSectionsUseCaseImpl
import pt.socialfood.domain.usecase.home.ObserveHomeSectionsUseCase
import pt.socialfood.domain.usecase.home.ObserveHomeSectionsUseCaseImpl
import pt.socialfood.domain.usecase.home.RemoveHomeSectionItemUseCase
import pt.socialfood.domain.usecase.home.RemoveHomeSectionItemUseCaseImpl
import pt.socialfood.domain.usecase.home.UpdateHomeSectionUseCase
import pt.socialfood.domain.usecase.home.UpdateHomeSectionUseCaseImpl
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
import pt.socialfood.domain.usecase.restaurant.AddRestaurantByPlaceIdUseCase
import pt.socialfood.domain.usecase.restaurant.AddRestaurantByPlaceIdUseCaseImpl
import pt.socialfood.domain.usecase.restaurant.AwaitEnrichedRestaurantByPlaceIdUseCase
import pt.socialfood.domain.usecase.restaurant.AwaitEnrichedRestaurantByPlaceIdUseCaseImpl
import pt.socialfood.domain.usecase.restaurant.DeleteRestaurantUseCase
import pt.socialfood.domain.usecase.restaurant.DeleteRestaurantUseCaseImpl
import pt.socialfood.domain.usecase.restaurant.FindRestaurantsUseCase
import pt.socialfood.domain.usecase.restaurant.FindRestaurantsUseCaseImpl
import pt.socialfood.domain.usecase.restaurant.GetRestaurantByIdUseCase
import pt.socialfood.domain.usecase.restaurant.GetRestaurantByIdUseCaseImpl
import pt.socialfood.domain.usecase.restaurant.GetRestaurantByPlaceIdUseCase
import pt.socialfood.domain.usecase.restaurant.GetRestaurantByPlaceIdUseCaseImpl
import pt.socialfood.domain.usecase.restaurant.GetRestaurantsUseCase
import pt.socialfood.domain.usecase.restaurant.GetRestaurantsUseCaseImpl
import pt.socialfood.domain.usecase.restaurant.UpdateRestaurantUseCase
import pt.socialfood.domain.usecase.restaurant.UpdateRestaurantUseCaseImpl
import pt.socialfood.domain.usecase.search.GetGuideSuggestionsUseCase
import pt.socialfood.domain.usecase.search.GetGuideSuggestionsUseCaseImpl
import pt.socialfood.domain.usecase.search.GetRestaurantSuggestionsUseCase
import pt.socialfood.domain.usecase.search.GetRestaurantSuggestionsUseCaseImpl
import pt.socialfood.domain.usecase.search.SearchUseCase
import pt.socialfood.domain.usecase.search.SearchUseCaseImpl
import pt.socialfood.domain.usecase.user.FindUsersUseCase
import pt.socialfood.domain.usecase.user.FindUsersUseCaseImpl
import pt.socialfood.domain.usecase.user.GetPresignedUrlUseCase
import pt.socialfood.domain.usecase.user.GetPresignedUrlUseCaseImpl
import pt.socialfood.domain.usecase.user.GetUserByIdUseCase
import pt.socialfood.domain.usecase.user.GetUserByIdUseCaseImpl
import pt.socialfood.domain.usecase.user.GetUserMeUseCase
import pt.socialfood.domain.usecase.user.GetUserMeUseCaseImpl
import pt.socialfood.domain.usecase.user.GetUsersUseCase
import pt.socialfood.domain.usecase.user.GetUsersUseCaseImpl
import pt.socialfood.domain.usecase.user.ObserveUserUseCase
import pt.socialfood.domain.usecase.user.ObserveUserUseCaseImpl
import pt.socialfood.domain.usecase.user.UpdateUserPhotoUseCase
import pt.socialfood.domain.usecase.user.UpdateUserPhotoUseCaseImpl
import pt.socialfood.domain.usecase.user.UpdateUserUseCase
import pt.socialfood.domain.usecase.user.UpdateUserUseCaseImpl
import pt.socialfood.presentation.author.detail.AuthorDetailViewModel
import pt.socialfood.presentation.author.list.AuthorsViewModel
import pt.socialfood.presentation.favourite.guide.FavouriteGuidesViewModel
import pt.socialfood.presentation.favourite.restaurant.FavouriteRestaurantsViewModel
import pt.socialfood.presentation.guide.create.CreateGuideViewModel
import pt.socialfood.presentation.guide.detail.GuideDetailViewModel
import pt.socialfood.presentation.guide.edit.EditGuideViewModel
import pt.socialfood.presentation.guide.list.GuidesViewModel
import pt.socialfood.presentation.home.HomeViewModel
import pt.socialfood.presentation.profile.ProfileViewModel
import pt.socialfood.presentation.profile.edit.EditProfileViewModel
import pt.socialfood.presentation.restaurant.detail.RestaurantDetailViewModel
import pt.socialfood.presentation.restaurant.search.SearchRestaurantsViewModel
import pt.socialfood.presentation.search.SearchViewModel
import pt.socialfood.presentation.signin.SignInViewModel
import pt.socialfood.presentation.signup.SignUpViewModel
import pt.socialfood.presentation.startup.StartupViewModel
import pt.socialfood.presentation.validatecode.ValidateCodeViewModel

expect val platformModule: Module

val networkModule =
    module {
        single { AppImageLoaderFactory(get<ImageHttpClient>().client) }
        single<AuthApi> { AuthApiImpl(get()) }
        single<AuthorsApi> { AuthorsApiImpl(get()) }
        single<ConfigsApi> { ConfigsApiImpl(get()) }
        single<FavouriteRestaurantsApi> { FavouriteRestaurantsApiImpl(get()) }
        single<FavouritesGuidesApi> { FavouritesGuidesApiImpl(get()) }
        single<GuidesApi> { GuidesApiImpl(get()) }
        single<HomeApi> { HomeApiImpl(get()) }
        single<HttpClient> { get<KtorHttpClient>().client }
        single<ImageCache> { get<AppImageLoaderFactory>() }
        single { ImageHttpClient() }
        single { KtorHttpClient(get()) }
        single<PlacesApi> { PlacesApiImpl(get()) }
        single<RestaurantApi> { RestaurantApiImpl(get()) }
        single<S3Api> { S3ApiImpl(get<S3HttpClient>().client) }
        single { S3HttpClient() }
        single<SearchApi> { SearchApiImpl(get()) }
        single { SessionManager(get()) }
        single<UserApi> { UserApiImpl(get()) }
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
            FavouriteRestaurantsRepositoryImpl(get(), get<AppDatabase>().favouriteRestaurantDao(), get())
        }
        single<FavouritesGuidesRepository> {
            FavouritesGuidesRepositoryImpl(get(), get<AppDatabase>().favouriteDao(), get())
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
        single<PhotosRepository> { PhotosRepositoryImpl(get()) }
        single<PlacesRepository> { PlacesRepositoryImpl(get()) }
        single<RestaurantsRepository> { RestaurantsRepositoryImpl(get()) }
        single<SearchRepository> { SearchRepositoryImpl(get()) }
        single<UsersRepository> { UsersRepositoryImpl(get()) }
    }

val useCaseModule =
    module {
        factory<AddHomeSectionItemUseCase> { AddHomeSectionItemUseCaseImpl(get()) }
        factory<AddRestaurantByPlaceIdUseCase> { AddRestaurantByPlaceIdUseCaseImpl(get()) }
        factory<AddRestaurantGuideUseCase> { AddRestaurantGuideUseCaseImpl(get(), get()) }
        factory<AwaitEnrichedRestaurantByPlaceIdUseCase> { AwaitEnrichedRestaurantByPlaceIdUseCaseImpl(get()) }
        factory<CreateGuideUseCase> { CreateGuideUseCaseImpl(get(), get()) }
        factory<CreateHomeSectionUseCase> { CreateHomeSectionUseCaseImpl(get()) }
        factory<DeleteGuideUseCase> { DeleteGuideUseCaseImpl(get()) }
        factory<DeleteHomeSectionUseCase> { DeleteHomeSectionUseCaseImpl(get()) }
        factory<DeleteRestaurantUseCase> { DeleteRestaurantUseCaseImpl(get()) }
        factory<FindAuthorsUseCase> { FindAuthorsUseCaseImpl(get()) }
        factory<FindGuidesUseCase> { FindGuidesUseCaseImpl(get()) }
        factory<FindRestaurantsUseCase> { FindRestaurantsUseCaseImpl(get()) }
        factory<FindUsersUseCase> { FindUsersUseCaseImpl(get()) }
        factory<GetAuthorByIdUseCase> { GetAuthorByIdUseCaseImpl(get()) }
        factory<GetAuthorsPagingUseCase> { GetAuthorsPagingUseCaseImpl(get()) }
        factory<GetAuthorsUseCase> { GetAuthorsUseCaseImpl(get()) }
        factory<GetConfigsUseCase> { GetConfigsUseCaseImpl(get()) }
        factory<GetFavouriteGuidesUseCase> { GetFavouriteGuidesUseCaseImpl(get()) }
        factory<GetFavouriteRestaurantsUseCase> { GetFavouriteRestaurantsUseCaseImpl(get()) }
        factory<GetGuideByIdUseCase> { GetGuideByIdUseCaseImpl(get()) }
        factory<GetGuideSuggestionsUseCase> { GetGuideSuggestionsUseCaseImpl(get()) }
        factory<GetGuidesPagingUseCase> { GetGuidesPagingUseCaseImpl(get()) }
        factory<GetGuidesUseCase> { GetGuidesUseCaseImpl(get()) }
        factory<GetHomeSectionByIdUseCase> { GetHomeSectionByIdUseCaseImpl(get()) }
        factory<GetHomeSectionsUseCase> { GetHomeSectionsUseCaseImpl(get()) }
        factory<GetPresignedUrlUseCase> { GetPresignedUrlUseCaseImpl(get()) }
        factory<GetRestaurantByIdUseCase> { GetRestaurantByIdUseCaseImpl(get()) }
        factory<GetRestaurantByPlaceIdUseCase> { GetRestaurantByPlaceIdUseCaseImpl(get()) }
        factory<GetRestaurantSuggestionsUseCase> { GetRestaurantSuggestionsUseCaseImpl(get()) }
        factory<GetRestaurantsUseCase> { GetRestaurantsUseCaseImpl(get()) }
        factory<GetUserByIdUseCase> { GetUserByIdUseCaseImpl(get()) }
        factory<GetUserMeUseCase> { GetUserMeUseCaseImpl(get()) }
        factory<GetUsersUseCase> { GetUsersUseCaseImpl(get()) }
        factory<IsGuideFavouriteUseCase> { IsGuideFavouriteUseCaseImpl(get()) }
        factory<IsRestaurantFavouriteUseCase> { IsRestaurantFavouriteUseCaseImpl(get()) }
        factory<LoginUseCase> { LoginUseCaseImpl(get(), get()) }
        factory<LoginWithGoogleUseCase> { LoginWithGoogleUseCaseImpl(get(), get()) }
        factory<LogoutUseCase> { LogoutUseCaseImpl(get(), get()) }
        factory<MarkGuideFavouriteUseCase> { MarkGuideFavouriteUseCaseImpl(get()) }
        factory<MarkRestaurantFavouriteUseCase> { MarkRestaurantFavouriteUseCaseImpl(get()) }
        factory<ObserveFavouriteGuideIdsUseCase> { ObserveFavouriteGuideIdsUseCaseImpl(get()) }
        factory<ObserveHomeSectionsUseCase> { ObserveHomeSectionsUseCaseImpl(get()) }
        factory<ObserveUserUseCase> { ObserveUserUseCaseImpl(get()) }
        factory<RegisterUseCase> { RegisterUseCaseImpl(get(), get()) }
        factory<RemoveHomeSectionItemUseCase> { RemoveHomeSectionItemUseCaseImpl(get()) }
        factory<ResendVerificationCodeUseCase> { ResendVerificationCodeUseCaseImpl(get()) }
        factory<RestartSignUpUseCase> { RestartSignUpUseCaseImpl(get()) }
        factory<SearchPlacesUseCase> { SearchPlacesUseCaseImpl(get()) }
        factory<SearchUseCase> { SearchUseCaseImpl(get()) }
        factory<SyncFavouriteRestaurantsUseCase> { SyncFavouriteRestaurantsUseCaseImpl(get()) }
        factory<SyncFavouritesUseCase> { SyncFavouritesUseCaseImpl(get()) }
        factory<UnmarkGuideFavouriteUseCase> { UnmarkGuideFavouriteUseCaseImpl(get()) }
        factory<UnmarkRestaurantFavouriteUseCase> { UnmarkRestaurantFavouriteUseCaseImpl(get()) }
        factory<UpdateGuideUseCase> { UpdateGuideUseCaseImpl(get(), get()) }
        factory<UpdateHomeSectionUseCase> { UpdateHomeSectionUseCaseImpl(get()) }
        factory<UpdateRestaurantUseCase> { UpdateRestaurantUseCaseImpl(get()) }
        factory<UpdateUserPhotoUseCase> { UpdateUserPhotoUseCaseImpl(get()) }
        factory<UpdateUserUseCase> { UpdateUserUseCaseImpl(get()) }
        factory<UploadPhotoUseCase> { UploadPhotoUseCaseImpl(get()) }
        factory<ValidateCodeUseCase> { ValidateCodeUseCaseImpl(get(), get(), get()) }
    }

val viewModelModule =
    module {
        factory { (authorId: String) -> AuthorDetailViewModel(get(), authorId) }
        factory { AuthorsViewModel(get(), get()) }
        factory { CreateGuideViewModel(get(), get(), get()) }
        factory { (guideId: String) -> EditGuideViewModel(get(), get(), get(), get(), get(), guideId) }
        factory { EditProfileViewModel(get(), get(), get(), get(), get(), get()) }
        factory { FavouriteGuidesViewModel(get(), get()) }
        factory { FavouriteRestaurantsViewModel(get(), get()) }
        factory { (guideId: String) -> GuideDetailViewModel(get(), get(), get(), get(), get(), guideId) }
        factory { GuidesViewModel(get(), get(), get(), get(), get()) }
        factory { HomeViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
        factory { ProfileViewModel(get(), get(), get()) }
        factory { (restaurantId: String) -> RestaurantDetailViewModel(get(), get(), get(), get(), restaurantId) }
        factory { (guideId: String) -> SearchRestaurantsViewModel(get(), get(), get()) }
        factory { SearchViewModel(get(), get(), get()) }
        factory { SignInViewModel(get(), get()) }
        factory { SignUpViewModel(get()) }
        factory { StartupViewModel(get(), get(), get()) }
        factory { (email: String) -> ValidateCodeViewModel(get(), get(), get(), email) }
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
                    )
                },
            )
            printLogger(Level.DEBUG)
        }

    SingletonImageLoader.setSafe(koinApplication.koin.get<AppImageLoaderFactory>())
}
