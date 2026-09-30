package pt.socialfood.presentation.profile.di

import org.koin.dsl.module
import pt.socialfood.presentation.profile.edit.EditProfileViewModel

val profileFeatureModule =
    module {
        factory { EditProfileViewModel(get(), get(), get()) }
    }
