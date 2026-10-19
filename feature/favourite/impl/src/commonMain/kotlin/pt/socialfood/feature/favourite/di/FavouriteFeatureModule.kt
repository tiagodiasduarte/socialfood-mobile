package pt.socialfood.feature.favourite.di

import org.koin.dsl.module
import pt.socialfood.feature.favourite.guide.FavouriteGuidesViewModel
import pt.socialfood.feature.favourite.restaurant.FavouriteRestaurantsViewModel

val favouriteFeatureModule =
    module {
        factory { FavouriteGuidesViewModel(get(), get()) }
        factory { FavouriteRestaurantsViewModel(get(), get()) }
    }
