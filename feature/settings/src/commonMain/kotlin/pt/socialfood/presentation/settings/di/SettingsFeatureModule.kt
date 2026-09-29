package pt.socialfood.presentation.settings.di

import org.koin.dsl.module
import pt.socialfood.presentation.drawer.DrawerViewModel
import pt.socialfood.presentation.theme.ThemeViewModel

val settingsFeatureModule =
    module {
        factory { DrawerViewModel(get(), get(), get()) }
        factory { ThemeViewModel(get(), get()) }
    }
