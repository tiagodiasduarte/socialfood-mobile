package pt.socialfood.feature.home.di

import org.koin.dsl.module
import pt.socialfood.feature.home.HomeViewModel

val homeFeatureModule =
    module {
        factory { HomeViewModel(get(), get(), get(), get()) }
    }
