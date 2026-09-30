package pt.socialfood.presentation.home.di

import org.koin.dsl.module
import pt.socialfood.presentation.home.HomeViewModel

val homeFeatureModule =
    module {
        factory { HomeViewModel(get(), get(), get(), get()) }
    }
