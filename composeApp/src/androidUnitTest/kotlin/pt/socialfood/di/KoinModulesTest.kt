package pt.socialfood.di

import android.content.Context
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify
import pt.socialfood.domain.model.VisitStatus
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
import kotlin.test.Test

@OptIn(KoinExperimentalAPI::class)
class KoinModulesTest {

    @Test
    fun `given all app modules when verified then every constructor dependency has a definition`() {
        // Given
        val appModule = module {
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
        }

        // When / Then
        // Provided outside Koin definitions:
        // - Context: androidContext()
        // - String, VisitStatus: runtime parameters (ids, email, visit status)
        // - HttpClientEngine, HttpClientConfig: HttpClient is bound as KtorHttpClient.client, never constructed by Koin
        appModule.verify(
            extraTypes = listOf(
                Context::class,
                String::class,
                VisitStatus::class,
                HttpClientEngine::class,
                HttpClientConfig::class,
            ),
        )
    }
}
