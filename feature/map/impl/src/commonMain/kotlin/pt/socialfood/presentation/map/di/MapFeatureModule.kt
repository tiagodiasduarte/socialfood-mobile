package pt.socialfood.presentation.map.di

import org.koin.dsl.module
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.presentation.map.restaurant.MapRestaurantViewModel
import pt.socialfood.presentation.map.restaurant.visitstatus.MapRestaurantVisitStatusViewModel

val mapFeatureModule =
    module {
        factory { (restaurantId: String) -> MapRestaurantViewModel(get(), restaurantId) }
        factory { (status: VisitStatus) -> MapRestaurantVisitStatusViewModel(get(), status) }
    }
