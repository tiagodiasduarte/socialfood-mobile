package pt.socialfood.feature.restaurant.di

import org.koin.dsl.module
import pt.socialfood.feature.restaurant.detail.RestaurantDetailViewModel
import pt.socialfood.feature.restaurant.navigation.RestaurantPickerResults
import pt.socialfood.feature.restaurant.navigation.RestaurantPickerResultsImpl
import pt.socialfood.feature.restaurant.search.SearchRestaurantsViewModel
import pt.socialfood.feature.restaurant.visited.RestaurantVisitedViewModel
import pt.socialfood.feature.restaurant.wishlist.RestaurantWishlistViewModel

val restaurantFeatureModule =
    module {
        single<RestaurantPickerResults> { RestaurantPickerResultsImpl() }
        factory { (restaurantId: String) ->
            RestaurantDetailViewModel(get(), get(), get(), restaurantId)
        }
        factory { RestaurantVisitedViewModel(get(), get(), get()) }
        factory { RestaurantWishlistViewModel(get(), get(), get()) }
        factory { (requestKey: String) ->
            SearchRestaurantsViewModel(get(), get(), get(), get(), get(), requestKey)
        }
    }
