package pt.socialfood.presentation.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.ExperimentalSerializationApi
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
 * Every route type must be registered here, or restoring the back stack (e.g. after process death)
 * fails to find its serializer. Registering each feature's sealed route interface pulls in all of
 * its subclasses, so new routes inside an existing feature are picked up automatically.
 */
@OptIn(ExperimentalSerializationApi::class)
val serializersConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclassesOfSealed<AuthorRoute>()
            subclassesOfSealed<FavouriteRoute>()
            subclassesOfSealed<GuideRoute>()
            subclassesOfSealed<HomeRoute>()
            subclassesOfSealed<MapRoute>()
            subclassesOfSealed<ProfileRoute>()
            subclassesOfSealed<RestaurantRoute>()
            subclassesOfSealed<SearchRoute>()
        }
    }
}
