package pt.socialfood.presentation.restaurant.di

import org.koin.dsl.module
import pt.socialfood.presentation.restaurant.detail.RestaurantDetailViewModel
import pt.socialfood.presentation.restaurant.navigation.RestaurantPickerResults
import pt.socialfood.presentation.restaurant.navigation.RestaurantPickerResultsImpl
import pt.socialfood.presentation.restaurant.search.SearchRestaurantsViewModel
import pt.socialfood.presentation.restaurant.visited.RestaurantVisitedViewModel
import pt.socialfood.presentation.restaurant.wishlist.RestaurantWishlistViewModel

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
