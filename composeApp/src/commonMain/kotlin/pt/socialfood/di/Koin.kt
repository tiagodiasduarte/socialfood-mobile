package pt.socialfood.di

import coil3.SingletonImageLoader
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.dsl.module
import pt.socialfood.core.data.di.coreDataModule
import pt.socialfood.core.database.di.coreDatabaseModule
import pt.socialfood.core.datastore.di.coreDatastoreModule
import pt.socialfood.core.network.di.coreNetworkModule
import pt.socialfood.data.network.CoilHttpClient
import pt.socialfood.domain.session.SessionManager
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
import pt.socialfood.domain.usecase.login.RestartSignUpUseCase
import pt.socialfood.domain.usecase.login.RestartSignUpUseCaseImpl
import pt.socialfood.domain.usecase.login.ValidateCodeUseCase
import pt.socialfood.domain.usecase.login.ValidateCodeUseCaseImpl
import pt.socialfood.domain.usecase.search.SaveRecentSearchUseCase
import pt.socialfood.domain.usecase.search.SaveRecentSearchUseCaseImpl
import pt.socialfood.domain.usecase.search.SaveRecentSearchedPlaceUseCase
import pt.socialfood.domain.usecase.search.SaveRecentSearchedPlaceUseCaseImpl
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

val appModule =
    module {
        single { AppImageLoaderFactory(get<CoilHttpClient>().client) }
        single<ImageCache> { get<AppImageLoaderFactory>() }
        single { SessionManager(get()) }
    }

val useCaseModule =
    module {
        factory<AddRestaurantGuideUseCase> { AddRestaurantGuideUseCaseImpl(get(), get()) }
        factory<CreateGuideUseCase> { CreateGuideUseCaseImpl(get(), get()) }
        factory<LoginUseCase> { LoginUseCaseImpl(get(), get()) }
        factory<LoginWithGoogleUseCase> { LoginWithGoogleUseCaseImpl(get(), get()) }
        factory<LogoutUseCase> { LogoutUseCaseImpl(get(), get(), get(), get()) }
        factory<ObserveUserUseCase> { ObserveUserUseCaseImpl(get()) }
        factory<RegisterUseCase> { RegisterUseCaseImpl(get(), get()) }
        factory<RestartSignUpUseCase> { RestartSignUpUseCaseImpl(get()) }
        factory<SaveRecentSearchUseCase> { SaveRecentSearchUseCaseImpl(get()) }
        factory<SaveRecentSearchedPlaceUseCase> { SaveRecentSearchedPlaceUseCaseImpl(get()) }
        factory<SetThemeModeUseCase> { SetThemeModeUseCaseImpl(get()) }
        factory<UpdateGuideUseCase> { UpdateGuideUseCaseImpl(get(), get()) }
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
                        coreNetworkModule,
                        coreDatabaseModule,
                        coreDatastoreModule,
                        coreDataModule,
                        platformModule,
                        appModule,
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
