package pt.socialfood.feature.profile.di

import org.koin.dsl.module
import pt.socialfood.feature.profile.edit.EditProfileViewModel

val profileFeatureModule =
    module {
        factory { EditProfileViewModel(get(), get(), get()) }
    }
