package pt.socialfood.feature.search.di

import org.koin.dsl.module
import pt.socialfood.feature.search.SearchViewModel

val searchFeatureModule =
    module {
        factory { SearchViewModel(get(), get(), get()) }
    }
