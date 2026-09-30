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
import pt.socialfood.core.domain.di.coreDomainModule
import pt.socialfood.core.network.di.coreNetworkModule
import pt.socialfood.data.network.CoilHttpClient
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
                        coreDomainModule,
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
