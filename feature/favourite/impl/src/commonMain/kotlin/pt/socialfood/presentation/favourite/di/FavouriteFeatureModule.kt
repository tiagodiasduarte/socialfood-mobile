package pt.socialfood.presentation.favourite.di

import org.koin.dsl.module
import pt.socialfood.presentation.favourite.guide.FavouriteGuidesViewModel
import pt.socialfood.presentation.favourite.restaurant.FavouriteRestaurantsViewModel

val favouriteFeatureModule =
    module {
        factory { FavouriteGuidesViewModel(get(), get()) }
        factory { FavouriteRestaurantsViewModel(get(), get()) }
    }
