package pt.socialfood.core.domain.di

import org.koin.dsl.module
import pt.socialfood.domain.session.SessionManager
import pt.socialfood.domain.usecase.guide.AddRestaurantGuideUseCase
import pt.socialfood.domain.usecase.guide.AddRestaurantGuideUseCaseImpl
import pt.socialfood.domain.usecase.guide.CreateGuideUseCase
import pt.socialfood.domain.usecase.guide.CreateGuideUseCaseImpl
import pt.socialfood.domain.usecase.guide.UpdateGuideUseCase
import pt.socialfood.domain.usecase.guide.UpdateGuideUseCaseImpl
import pt.socialfood.domain.usecase.login.LoginUseCase
import pt.socialfood.domain.usecase.login.LoginUseCaseImpl
import pt.socialfood.domain.usecase.login.LoginWithGoogleUseCase
import pt.socialfood.domain.usecase.login.LoginWithGoogleUseCaseImpl
import pt.socialfood.domain.usecase.login.LogoutUseCase
import pt.socialfood.domain.usecase.login.LogoutUseCaseImpl
import pt.socialfood.domain.usecase.login.RegisterUseCase
import pt.socialfood.domain.usecase.login.RegisterUseCaseImpl
import pt.socialfood.domain.usecase.login.RestartSignUpUseCase
import pt.socialfood.domain.usecase.login.RestartSignUpUseCaseImpl
import pt.socialfood.domain.usecase.login.ValidateCodeUseCase
import pt.socialfood.domain.usecase.login.ValidateCodeUseCaseImpl
import pt.socialfood.domain.usecase.search.SaveRecentSearchUseCase
import pt.socialfood.domain.usecase.search.SaveRecentSearchUseCaseImpl
import pt.socialfood.domain.usecase.search.SaveRecentSearchedPlaceUseCase
import pt.socialfood.domain.usecase.search.SaveRecentSearchedPlaceUseCaseImpl
import pt.socialfood.domain.usecase.theme.SetThemeModeUseCase
import pt.socialfood.domain.usecase.theme.SetThemeModeUseCaseImpl
import pt.socialfood.domain.usecase.user.ObserveUserUseCase
import pt.socialfood.domain.usecase.user.ObserveUserUseCaseImpl

val coreDomainModule =
    module {
        factory<AddRestaurantGuideUseCase> { AddRestaurantGuideUseCaseImpl(get(), get()) }
        factory<CreateGuideUseCase> { CreateGuideUseCaseImpl(get(), get()) }
        factory<LoginUseCase> { LoginUseCaseImpl(get(), get()) }
        factory<LoginWithGoogleUseCase> { LoginWithGoogleUseCaseImpl(get(), get()) }
        factory<LogoutUseCase> { LogoutUseCaseImpl(get(), get(), get(), get()) }
        factory<ObserveUserUseCase> { ObserveUserUseCaseImpl(get()) }
        factory<RegisterUseCase> { RegisterUseCaseImpl(get(), get()) }
        factory<RestartSignUpUseCase> { RestartSignUpUseCaseImpl(get()) }
        factory<SaveRecentSearchUseCase> { SaveRecentSearchUseCaseImpl(get()) }
        factory<SaveRecentSearchedPlaceUseCase> { SaveRecentSearchedPlaceUseCaseImpl(get()) }
        factory<SetThemeModeUseCase> { SetThemeModeUseCaseImpl(get()) }
        factory<UpdateGuideUseCase> { UpdateGuideUseCaseImpl(get(), get()) }
        factory<ValidateCodeUseCase> { ValidateCodeUseCaseImpl(get(), get(), get()) }
        single { SessionManager(get()) }
    }
