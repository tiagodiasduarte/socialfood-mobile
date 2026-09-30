package pt.socialfood.presentation.search.di

import org.koin.dsl.module
import pt.socialfood.presentation.search.SearchViewModel

val searchFeatureModule =
    module {
        factory { SearchViewModel(get(), get(), get()) }
    }
