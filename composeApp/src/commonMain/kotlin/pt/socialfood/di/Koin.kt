package pt.socialfood.di

import coil3.SingletonImageLoader
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.dsl.module
import pt.socialfood.data.AuthApi
import pt.socialfood.data.AuthApiImpl
import pt.socialfood.data.AuthorsApi
import pt.socialfood.data.AuthorsApiImpl
import pt.socialfood.data.ConfigsApi
import pt.socialfood.data.ConfigsApiImpl
import pt.socialfood.data.FavouriteRestaurantsApi
import pt.socialfood.data.FavouriteRestaurantsApiImpl
import pt.socialfood.data.FavouritesApi
import pt.socialfood.data.FavouritesApiImpl
import pt.socialfood.data.GuidesApi
import pt.socialfood.data.GuidesApiImpl
import pt.socialfood.data.HomeApi
import pt.socialfood.data.HomeApiImpl
import pt.socialfood.data.PlacesApi
import pt.socialfood.data.PlacesApiImpl
import pt.socialfood.data.RestaurantApi
import pt.socialfood.data.RestaurantApiImpl
import pt.socialfood.data.S3Api
import pt.socialfood.data.S3ApiImpl
import pt.socialfood.data.UserApi
import pt.socialfood.data.UserApiImpl
import pt.socialfood.data.local.AppDatabase
import pt.socialfood.data.network.ImageHttpClient
import pt.socialfood.data.network.KtorHttpClient
import pt.socialfood.data.network.S3HttpClient
import pt.socialfood.data.network.SessionManager
import pt.socialfood.data.repository.AuthRepositoryImpl
import pt.socialfood.data.repository.AuthorsRepositoryImpl
import pt.socialfood.data.repository.PhotosRepositoryImpl
import pt.socialfood.data.repository.ConfigsRepositoryImpl
import pt.socialfood.data.repository.FavouriteRestaurantsRepositoryImpl
import pt.socialfood.data.repository.FavouritesRepositoryImpl
import pt.socialfood.data.repository.GuidesRepositoryImpl
import pt.socialfood.data.repository.HomeRepositoryImpl
import pt.socialfood.data.repository.PlacesRepositoryImpl
import pt.socialfood.data.repository.RestaurantsRepositoryImpl
import pt.socialfood.data.repository.UsersRepositoryImpl
import pt.socialfood.domain.repository.AuthRepository
import pt.socialfood.domain.repository.AuthorsRepository
import pt.socialfood.domain.repository.PhotosRepository
import pt.socialfood.domain.repository.ConfigsRepository
import pt.socialfood.domain.repository.FavouriteRestaurantsRepository
import pt.socialfood.domain.repository.FavouritesRepository
import pt.socialfood.domain.repository.GuidesRepository
import pt.socialfood.domain.repository.HomeRepository
import pt.socialfood.domain.repository.PlacesRepository
import pt.socialfood.domain.repository.RestaurantsRepository
import pt.socialfood.domain.repository.UsersRepository
import pt.socialfood.domain.use_case.SearchPlacesUseCase
import pt.socialfood.domain.use_case.SearchPlacesUseCaseImpl
import pt.socialfood.domain.use_case.author.FindAuthorsUseCase
import pt.socialfood.domain.use_case.author.FindAuthorsUseCaseImpl
import pt.socialfood.domain.use_case.author.GetAuthorByIdUseCase
import pt.socialfood.domain.use_case.author.GetAuthorByIdUseCaseImpl
import pt.socialfood.domain.use_case.author.GetAuthorsUseCase
import pt.socialfood.domain.use_case.author.GetAuthorsUseCaseImpl
import pt.socialfood.domain.use_case.configs.GetConfigsUseCase
import pt.socialfood.domain.use_case.configs.GetConfigsUseCaseImpl
import pt.socialfood.domain.use_case.favourite.guide.GetFavouriteGuidesUseCase
import pt.socialfood.domain.use_case.favourite.guide.GetFavouriteGuidesUseCaseImpl
import pt.socialfood.domain.use_case.favourite.guide.IsGuideFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.guide.IsGuideFavouriteUseCaseImpl
import pt.socialfood.domain.use_case.favourite.guide.MarkGuideFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.guide.MarkGuideFavouriteUseCaseImpl
import pt.socialfood.domain.use_case.favourite.SyncFavouriteRestaurantsUseCase
import pt.socialfood.domain.use_case.favourite.SyncFavouriteRestaurantsUseCaseImpl
import pt.socialfood.domain.use_case.favourite.SyncFavouritesUseCase
import pt.socialfood.domain.use_case.favourite.SyncFavouritesUseCaseImpl
import pt.socialfood.domain.use_case.favourite.guide.UnmarkGuideFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.guide.UnmarkGuideFavouriteUseCaseImpl
import pt.socialfood.domain.use_case.favourite.restaurant.GetFavouriteRestaurantsUseCase
import pt.socialfood.domain.use_case.favourite.restaurant.GetFavouriteRestaurantsUseCaseImpl
import pt.socialfood.domain.use_case.favourite.restaurant.IsRestaurantFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.restaurant.IsRestaurantFavouriteUseCaseImpl
import pt.socialfood.domain.use_case.favourite.restaurant.MarkRestaurantFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.restaurant.MarkRestaurantFavouriteUseCaseImpl
import pt.socialfood.domain.use_case.favourite.restaurant.UnmarkRestaurantFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.restaurant.UnmarkRestaurantFavouriteUseCaseImpl
import pt.socialfood.domain.use_case.guide.AddRestaurantGuideUseCase
import pt.socialfood.domain.use_case.guide.AddRestaurantGuideUseCaseImpl
import pt.socialfood.domain.use_case.guide.CreateGuideUseCase
import pt.socialfood.domain.use_case.guide.CreateGuideUseCaseImpl
import pt.socialfood.domain.use_case.guide.DeleteGuideUseCase
import pt.socialfood.domain.use_case.guide.DeleteGuideUseCaseImpl
import pt.socialfood.domain.use_case.guide.GetGuideByIdUseCase
import pt.socialfood.domain.use_case.guide.GetGuideByIdUseCaseImpl
import pt.socialfood.domain.use_case.guide.FindGuidesUseCase
import pt.socialfood.domain.use_case.guide.FindGuidesUseCaseImpl
import pt.socialfood.domain.use_case.guide.GetGuidesUseCase
import pt.socialfood.domain.use_case.guide.GetGuidesUseCaseImpl
import pt.socialfood.domain.use_case.guide.UpdateGuideUseCase
import pt.socialfood.domain.use_case.guide.UpdateGuideUseCaseImpl
import pt.socialfood.domain.use_case.home.AddHomeSectionItemUseCase
import pt.socialfood.domain.use_case.home.AddHomeSectionItemUseCaseImpl
import pt.socialfood.domain.use_case.home.CreateHomeSectionUseCase
import pt.socialfood.domain.use_case.home.CreateHomeSectionUseCaseImpl
import pt.socialfood.domain.use_case.home.DeleteHomeSectionUseCase
import pt.socialfood.domain.use_case.home.DeleteHomeSectionUseCaseImpl
import pt.socialfood.domain.use_case.home.GetHomeSectionByIdUseCase
import pt.socialfood.domain.use_case.home.GetHomeSectionByIdUseCaseImpl
import pt.socialfood.domain.use_case.home.GetHomeSectionsUseCase
import pt.socialfood.domain.use_case.home.GetHomeSectionsUseCaseImpl
import pt.socialfood.domain.use_case.home.RemoveHomeSectionItemUseCase
import pt.socialfood.domain.use_case.home.RemoveHomeSectionItemUseCaseImpl
import pt.socialfood.domain.use_case.home.UpdateHomeSectionUseCase
import pt.socialfood.domain.use_case.home.UpdateHomeSectionUseCaseImpl
import pt.socialfood.domain.use_case.login.LoginUseCase
import pt.socialfood.domain.use_case.login.LoginUseCaseImpl
import pt.socialfood.domain.use_case.login.LoginWithGoogleUseCase
import pt.socialfood.domain.use_case.login.LoginWithGoogleUseCaseImpl
import pt.socialfood.domain.use_case.login.LogoutUseCase
import pt.socialfood.domain.use_case.login.LogoutUseCaseImpl
import pt.socialfood.domain.use_case.login.RegisterUseCase
import pt.socialfood.domain.use_case.login.RegisterUseCaseImpl
import pt.socialfood.domain.use_case.login.ResendVerificationCodeUseCase
import pt.socialfood.domain.use_case.login.ResendVerificationCodeUseCaseImpl
import pt.socialfood.domain.use_case.login.RestartSignUpUseCase
import pt.socialfood.domain.use_case.login.RestartSignUpUseCaseImpl
import pt.socialfood.domain.use_case.login.ValidateCodeUseCase
import pt.socialfood.domain.use_case.login.ValidateCodeUseCaseImpl
import pt.socialfood.domain.use_case.restaurant.DeleteRestaurantUseCase
import pt.socialfood.domain.use_case.restaurant.DeleteRestaurantUseCaseImpl
import pt.socialfood.domain.use_case.restaurant.FindRestaurantsUseCase
import pt.socialfood.domain.use_case.restaurant.FindRestaurantsUseCaseImpl
import pt.socialfood.domain.use_case.restaurant.GetRestaurantByIdUseCase
import pt.socialfood.domain.use_case.restaurant.GetRestaurantByIdUseCaseImpl
import pt.socialfood.domain.use_case.restaurant.GetRestaurantsUseCase
import pt.socialfood.domain.use_case.restaurant.GetRestaurantsUseCaseImpl
import pt.socialfood.domain.use_case.restaurant.UpdateRestaurantUseCase
import pt.socialfood.domain.use_case.restaurant.UpdateRestaurantUseCaseImpl
import pt.socialfood.domain.use_case.restaurant.AddRestaurantByPlaceIdUseCase
import pt.socialfood.domain.use_case.restaurant.AddRestaurantByPlaceIdUseCaseImpl
import pt.socialfood.domain.use_case.restaurant.AwaitEnrichedRestaurantByPlaceIdUseCase
import pt.socialfood.domain.use_case.restaurant.AwaitEnrichedRestaurantByPlaceIdUseCaseImpl
import pt.socialfood.domain.use_case.restaurant.GetRestaurantByPlaceIdUseCase
import pt.socialfood.domain.use_case.restaurant.GetRestaurantByPlaceIdUseCaseImpl
import pt.socialfood.domain.use_case.user.GetUserByIdUseCase
import pt.socialfood.domain.use_case.user.GetUserByIdUseCaseImpl
import pt.socialfood.domain.use_case.user.GetUserMeUseCase
import pt.socialfood.domain.use_case.user.GetUserMeUseCaseImpl
import pt.socialfood.domain.use_case.user.FindUsersUseCase
import pt.socialfood.domain.use_case.user.FindUsersUseCaseImpl
import pt.socialfood.domain.use_case.user.GetUsersUseCase
import pt.socialfood.domain.use_case.user.GetUsersUseCaseImpl
import pt.socialfood.domain.use_case.photo.UploadPhotoUseCase
import pt.socialfood.domain.use_case.photo.UploadPhotoUseCaseImpl
import pt.socialfood.domain.use_case.user.GetPresignedUrlUseCase
import pt.socialfood.domain.use_case.user.GetPresignedUrlUseCaseImpl
import pt.socialfood.domain.use_case.user.ObserveUserUseCase
import pt.socialfood.domain.use_case.user.ObserveUserUseCaseImpl
import pt.socialfood.domain.use_case.user.UpdateUserPhotoUseCase
import pt.socialfood.domain.use_case.user.UpdateUserPhotoUseCaseImpl
import pt.socialfood.domain.use_case.user.UpdateUserUseCase
import pt.socialfood.domain.use_case.user.UpdateUserUseCaseImpl
import pt.socialfood.presentation.authors.detail.AuthorDetailViewModel
import pt.socialfood.presentation.authors.AuthorsViewModel
import pt.socialfood.presentation.favourite.guide.FavouriteGuidesViewModel
import pt.socialfood.presentation.favourite.FavouritesRestaurantsViewModel
import pt.socialfood.presentation.guides.detail.GuideDetailViewModel
import pt.socialfood.presentation.guides.GuidesViewModel
import pt.socialfood.presentation.guides.create.CreateGuideViewModel
import pt.socialfood.presentation.home.HomeViewModel
import pt.socialfood.presentation.sign_in.SignInViewModel
import pt.socialfood.presentation.sign_up.SignUpViewModel
import pt.socialfood.presentation.validate_code.ValidateCodeViewModel
import pt.socialfood.presentation.guides.edit.search_restaurants.SearchRestaurantsViewModel
import pt.socialfood.presentation.guides.edit.EditGuideViewModel
import pt.socialfood.presentation.profile.ProfileViewModel
import pt.socialfood.presentation.profile.edit.EditProfileViewModel
import pt.socialfood.presentation.restaurant.detail.RestaurantDetailViewModel
import pt.socialfood.presentation.splash.SplashViewModel

expect val platformModule: Module

val networkModule = module {
    single { SessionManager(get()) }
    single { KtorHttpClient(get()) }
    single<HttpClient> { get<KtorHttpClient>().client }
    single { S3HttpClient() }
    single<S3Api> { S3ApiImpl(get<S3HttpClient>().client) }
    single { ImageHttpClient() }
    single { AppImageLoaderFactory(get<ImageHttpClient>().client) }
    single<ImageCache> { get<AppImageLoaderFactory>() }
    single<AuthApi> { AuthApiImpl(get()) }
    single<AuthorsApi> { AuthorsApiImpl(get()) }
    single<ConfigsApi> { ConfigsApiImpl(get()) }
    single<FavouriteRestaurantsApi> { FavouriteRestaurantsApiImpl(get()) }
    single<FavouritesApi> { FavouritesApiImpl(get()) }
    single<GuidesApi> { GuidesApiImpl(get()) }
    single<HomeApi> { HomeApiImpl(get()) }
    single<PlacesApi> { PlacesApiImpl(get()) }
    single<RestaurantApi> { RestaurantApiImpl(get()) }
    single<UserApi> { UserApiImpl(get()) }
}

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<AuthorsRepository> { AuthorsRepositoryImpl(get()) }
    single<ConfigsRepository> { ConfigsRepositoryImpl(get()) }
    single<FavouriteRestaurantsRepository> { FavouriteRestaurantsRepositoryImpl(get(), get<AppDatabase>().favouriteRestaurantDao(), get()) }
    single<FavouritesRepository> { FavouritesRepositoryImpl(get(), get<AppDatabase>().favouriteDao(), get()) }
    single<GuidesRepository> { GuidesRepositoryImpl(get()) }
    single<HomeRepository> { HomeRepositoryImpl(get()) }
    single<PlacesRepository> { PlacesRepositoryImpl(get()) }
    single<RestaurantsRepository> { RestaurantsRepositoryImpl(get()) }
    single<UsersRepository> { UsersRepositoryImpl(get()) }
    single<PhotosRepository> { PhotosRepositoryImpl(get()) }
}

val useCaseModule = module {
    factory<FindGuidesUseCase> { FindGuidesUseCaseImpl(get()) }
    factory<GetGuidesUseCase> { GetGuidesUseCaseImpl(get()) }
    factory<GetGuideByIdUseCase> { GetGuideByIdUseCaseImpl(get()) }
    factory<CreateGuideUseCase> { CreateGuideUseCaseImpl(get(), get()) }
    factory<DeleteGuideUseCase> { DeleteGuideUseCaseImpl(get()) }
    factory<UpdateGuideUseCase> { UpdateGuideUseCaseImpl(get(), get()) }
    factory<AddRestaurantGuideUseCase> { AddRestaurantGuideUseCaseImpl(get(), get()) }

    factory<GetRestaurantsUseCase> { GetRestaurantsUseCaseImpl(get()) }
    factory<FindRestaurantsUseCase> { FindRestaurantsUseCaseImpl(get()) }
    factory<GetRestaurantByIdUseCase> { GetRestaurantByIdUseCaseImpl(get()) }
    factory<DeleteRestaurantUseCase> { DeleteRestaurantUseCaseImpl(get()) }
    factory<UpdateRestaurantUseCase> { UpdateRestaurantUseCaseImpl(get()) }

    factory<GetRestaurantByPlaceIdUseCase> { GetRestaurantByPlaceIdUseCaseImpl(get()) }
    factory<AddRestaurantByPlaceIdUseCase> { AddRestaurantByPlaceIdUseCaseImpl(get()) }
    factory<AwaitEnrichedRestaurantByPlaceIdUseCase> { AwaitEnrichedRestaurantByPlaceIdUseCaseImpl(get()) }
    factory<GetUsersUseCase> { GetUsersUseCaseImpl(get()) }
    factory<FindUsersUseCase> { FindUsersUseCaseImpl(get()) }
    factory<GetUserByIdUseCase> { GetUserByIdUseCaseImpl(get()) }
    factory<GetUserMeUseCase> { GetUserMeUseCaseImpl(get()) }
    factory<ObserveUserUseCase> { ObserveUserUseCaseImpl(get()) }
    factory<UploadPhotoUseCase> { UploadPhotoUseCaseImpl(get()) }
    factory<UpdateUserUseCase> { UpdateUserUseCaseImpl(get()) }
    factory<UpdateUserPhotoUseCase> { UpdateUserPhotoUseCaseImpl(get()) }

    factory<GetHomeSectionsUseCase> { GetHomeSectionsUseCaseImpl(get()) }
    factory<GetHomeSectionByIdUseCase> { GetHomeSectionByIdUseCaseImpl(get()) }
    factory<CreateHomeSectionUseCase> { CreateHomeSectionUseCaseImpl(get()) }
    factory<UpdateHomeSectionUseCase> { UpdateHomeSectionUseCaseImpl(get()) }
    factory<DeleteHomeSectionUseCase> { DeleteHomeSectionUseCaseImpl(get()) }
    factory<AddHomeSectionItemUseCase> { AddHomeSectionItemUseCaseImpl(get()) }
    factory<RemoveHomeSectionItemUseCase> { RemoveHomeSectionItemUseCaseImpl(get()) }

    factory<FindAuthorsUseCase> { FindAuthorsUseCaseImpl(get()) }
    factory<GetAuthorsUseCase> { GetAuthorsUseCaseImpl(get()) }
    factory<GetAuthorByIdUseCase> { GetAuthorByIdUseCaseImpl(get()) }

    factory<SearchPlacesUseCase> { SearchPlacesUseCaseImpl(get()) }

    factory<GetConfigsUseCase> { GetConfigsUseCaseImpl(get()) }
    factory<LoginUseCase> { LoginUseCaseImpl(get(), get()) }
    factory<LoginWithGoogleUseCase> { LoginWithGoogleUseCaseImpl(get(), get()) }
    factory<LogoutUseCase> { LogoutUseCaseImpl(get(), get()) }
    factory<RegisterUseCase> { RegisterUseCaseImpl(get(), get()) }
    factory<ValidateCodeUseCase> { ValidateCodeUseCaseImpl(get(), get(), get()) }
    factory<ResendVerificationCodeUseCase> { ResendVerificationCodeUseCaseImpl(get()) }
    factory<RestartSignUpUseCase> { RestartSignUpUseCaseImpl(get()) }

    factory<GetPresignedUrlUseCase> { GetPresignedUrlUseCaseImpl(get()) }

    factory<MarkGuideFavouriteUseCase> { MarkGuideFavouriteUseCaseImpl(get()) }
    factory<UnmarkGuideFavouriteUseCase> { UnmarkGuideFavouriteUseCaseImpl(get()) }
    factory<GetFavouriteGuidesUseCase> { GetFavouriteGuidesUseCaseImpl(get()) }
    factory<IsGuideFavouriteUseCase> { IsGuideFavouriteUseCaseImpl(get()) }
    factory<SyncFavouritesUseCase> { SyncFavouritesUseCaseImpl(get()) }

    factory<MarkRestaurantFavouriteUseCase> { MarkRestaurantFavouriteUseCaseImpl(get()) }
    factory<UnmarkRestaurantFavouriteUseCase> { UnmarkRestaurantFavouriteUseCaseImpl(get()) }
    factory<GetFavouriteRestaurantsUseCase> { GetFavouriteRestaurantsUseCaseImpl(get()) }
    factory<IsRestaurantFavouriteUseCase> { IsRestaurantFavouriteUseCaseImpl(get()) }
    factory<SyncFavouriteRestaurantsUseCase> { SyncFavouriteRestaurantsUseCaseImpl(get()) }
}

val viewModelModule = module {
    factory { SplashViewModel(get(), get(), get()) }

    factory { AuthorsViewModel(get<FindAuthorsUseCase>()) }
    factory { (authorId: String) -> AuthorDetailViewModel(get(), authorId) }

    factory { GuidesViewModel(get(), get()) }
    factory { (guideId: String) -> GuideDetailViewModel(get(), get(), get(), get(), get(), guideId) }
    factory { CreateGuideViewModel(get(), get(), get()) }
    factory { (guideId: String) -> EditGuideViewModel(get(), get(), get(), get(), get(), guideId) }

    factory { (guideId: String) -> SearchRestaurantsViewModel(get(), get(), get()) }

    factory { (restaurantId: String) -> RestaurantDetailViewModel(get(), get(), get(), get(), restaurantId) }

    factory { HomeViewModel(get()) }
    factory { SignInViewModel(get(), get()) }
    factory { SignUpViewModel(get()) }
    factory { (email: String) -> ValidateCodeViewModel(get(), get(), get(), email) }
    factory { ProfileViewModel(get(), get(), get()) }
    factory { FavouriteGuidesViewModel(get()) }
    factory { FavouritesRestaurantsViewModel(get()) }
    factory { EditProfileViewModel(get(), get(), get(), get(), get(), get()) }
}

fun initKoin(configuration: KoinAppDeclaration? = null) {
    val koinApplication = startKoin {
        includes(configuration)
        modules(
            module {
                includes(
                    platformModule,
                    networkModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule,
                )
            }
        )
        printLogger(Level.DEBUG)
    }

    SingletonImageLoader.setSafe(koinApplication.koin.get<AppImageLoaderFactory>())
}
