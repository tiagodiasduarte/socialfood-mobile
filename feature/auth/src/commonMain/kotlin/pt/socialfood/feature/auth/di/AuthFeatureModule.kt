package pt.socialfood.feature.auth.di

import org.koin.dsl.module
import pt.socialfood.feature.auth.signin.SignInViewModel
import pt.socialfood.feature.auth.signup.SignUpViewModel
import pt.socialfood.feature.auth.startup.StartupViewModel
import pt.socialfood.feature.auth.validatecode.ValidateCodeViewModel

val authFeatureModule =
    module {
        factory { SignInViewModel(get(), get(), get()) }
        factory { SignUpViewModel(get()) }
        factory { StartupViewModel(get(), get(), get()) }
        factory { (email: String) -> ValidateCodeViewModel(get(), get(), get(), email) }
    }
