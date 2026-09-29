package pt.socialfood.presentation.auth.di

import org.koin.dsl.module
import pt.socialfood.presentation.signin.SignInViewModel
import pt.socialfood.presentation.signup.SignUpViewModel
import pt.socialfood.presentation.startup.StartupViewModel
import pt.socialfood.presentation.validatecode.ValidateCodeViewModel

val authFeatureModule =
    module {
        factory { SignInViewModel(get(), get(), get()) }
        factory { SignUpViewModel(get()) }
        factory { StartupViewModel(get(), get(), get()) }
        factory { (email: String) -> ValidateCodeViewModel(get(), get(), get(), email) }
    }
