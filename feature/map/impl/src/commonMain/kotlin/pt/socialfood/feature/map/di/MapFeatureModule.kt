package pt.socialfood.feature.map.di

import org.koin.dsl.module
import pt.socialfood.core.model.VisitStatus
import pt.socialfood.feature.map.restaurant.MapRestaurantViewModel
import pt.socialfood.feature.map.restaurant.visitstatus.MapRestaurantVisitStatusViewModel

val mapFeatureModule =
    module {
        factory { (restaurantId: String) -> MapRestaurantViewModel(get(), restaurantId) }
        factory { (status: VisitStatus) -> MapRestaurantVisitStatusViewModel(get(), status) }
    }
