package pt.socialfood.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.json.Json
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.presentation.author.navigation.AuthorRoute
import pt.socialfood.presentation.favourite.navigation.FavouriteRoute
import pt.socialfood.presentation.guide.navigation.GuideRoute
import pt.socialfood.presentation.home.navigation.HomeRoute
import pt.socialfood.presentation.map.navigation.MapRoute
import pt.socialfood.presentation.profile.navigation.ProfileRoute
import pt.socialfood.presentation.restaurant.navigation.RestaurantRoute
import pt.socialfood.presentation.search.navigation.SearchRoute
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class NavKeySerializationTest {

    private val json = Json { serializersModule = serializersConfig.serializersModule }
    private val serializer = PolymorphicSerializer(NavKey::class)

    @Test
    fun `given every route when encoded and decoded as NavKey then the same route is restored`() {
        // Given
        val routes = listOf(
            AuthorRoute.Authors,
            AuthorRoute.AuthorDetail(Random.nextString()),
            AuthorRoute.Profile(Random.nextString()),
            FavouriteRoute.FavouriteGuides,
            FavouriteRoute.FavouriteRestaurants,
            GuideRoute.Guides,
            GuideRoute.GuideDetail(Random.nextString()),
            GuideRoute.GuideMap(Random.nextString(), Random.nextString(), Random.nextInt()),
            GuideRoute.CreateGuide,
            GuideRoute.EditGuide(Random.nextString(), Random.nextInt()),
            HomeRoute.Home,
            MapRoute.RestaurantMap(Random.nextString()),
            MapRoute.RestaurantsMap(VisitStatus.entries.random()),
            ProfileRoute.EditProfile,
            RestaurantRoute.RestaurantDetail(Random.nextString()),
            RestaurantRoute.AddRestaurants(Random.nextString()),
            RestaurantRoute.WishRestaurants,
            RestaurantRoute.AddWishRestaurant,
            RestaurantRoute.VisitedRestaurants,
            RestaurantRoute.AddVisitedRestaurant,
            SearchRoute.Search,
        )

        // When
        val restored = routes.map { route ->
            json.decodeFromString(serializer, json.encodeToString(serializer, route))
        }

        // Then
        assertEquals(routes, restored)
    }

    @Test
    fun `given the top level destinations when encoded and decoded as NavKey then every tab is restored`() {
        // Given
        val topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys.toList()

        // When
        val restored = topLevelRoutes.map { route ->
            json.decodeFromString(serializer, json.encodeToString(serializer, route))
        }

        // Then
        assertEquals(topLevelRoutes, restored)
    }
}
