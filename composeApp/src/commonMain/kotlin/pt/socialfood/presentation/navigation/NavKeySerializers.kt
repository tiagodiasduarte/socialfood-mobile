package pt.socialfood.presentation.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import pt.socialfood.presentation.author.navigation.AuthorRoute
import pt.socialfood.presentation.favourite.navigation.FavouriteRoute
import pt.socialfood.presentation.guide.navigation.GuideRoute
import pt.socialfood.presentation.home.navigation.HomeRoute
import pt.socialfood.presentation.map.navigation.MapRoute
import pt.socialfood.presentation.profile.navigation.ProfileRoute
import pt.socialfood.presentation.restaurant.navigation.RestaurantRoute
import pt.socialfood.presentation.search.navigation.SearchRoute

/**
 * Every feature's sealed route interface. Each one has to be listed here, or restoring the back
 * stack (e.g. after process death) fails to find its serializer. New routes inside an existing
 * feature are picked up automatically, and `NavKeySerializationTest` checks that every subclass of
 * every listed interface resolves.
 */
val featureRouteSerializers: List<KSerializer<out NavKey>> = listOf(
    AuthorRoute.serializer(),
    FavouriteRoute.serializer(),
    GuideRoute.serializer(),
    HomeRoute.serializer(),
    MapRoute.serializer(),
    ProfileRoute.serializer(),
    RestaurantRoute.serializer(),
    SearchRoute.serializer(),
)

@OptIn(ExperimentalSerializationApi::class)
val serializersConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            @Suppress("UNCHECKED_CAST")
            featureRouteSerializers.forEach { subclassesOfSealed(it as KSerializer<NavKey>) }
        }
    }
}
