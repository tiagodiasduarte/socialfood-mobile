package pt.socialfood.core.domain.di

import org.koin.dsl.module
import pt.socialfood.core.domain.session.SessionManager
import pt.socialfood.core.domain.usecase.guide.AddRestaurantGuideUseCase
import pt.socialfood.core.domain.usecase.guide.AddRestaurantGuideUseCaseImpl
import pt.socialfood.core.domain.usecase.guide.CreateGuideUseCase
import pt.socialfood.core.domain.usecase.guide.CreateGuideUseCaseImpl
import pt.socialfood.core.domain.usecase.guide.UpdateGuideUseCase
import pt.socialfood.core.domain.usecase.guide.UpdateGuideUseCaseImpl
import pt.socialfood.core.domain.usecase.login.LoginUseCase
import pt.socialfood.core.domain.usecase.login.LoginUseCaseImpl
import pt.socialfood.core.domain.usecase.login.LoginWithGoogleUseCase
import pt.socialfood.core.domain.usecase.login.LoginWithGoogleUseCaseImpl
import pt.socialfood.core.domain.usecase.login.LogoutUseCase
import pt.socialfood.core.domain.usecase.login.LogoutUseCaseImpl
import pt.socialfood.core.domain.usecase.login.RegisterUseCase
import pt.socialfood.core.domain.usecase.login.RegisterUseCaseImpl
import pt.socialfood.core.domain.usecase.login.RestartSignUpUseCase
import pt.socialfood.core.domain.usecase.login.RestartSignUpUseCaseImpl
import pt.socialfood.core.domain.usecase.login.ValidateCodeUseCase
import pt.socialfood.core.domain.usecase.login.ValidateCodeUseCaseImpl
import pt.socialfood.core.domain.usecase.search.SaveRecentSearchUseCase
import pt.socialfood.core.domain.usecase.search.SaveRecentSearchUseCaseImpl
import pt.socialfood.core.domain.usecase.search.SaveRecentSearchedPlaceUseCase
import pt.socialfood.core.domain.usecase.search.SaveRecentSearchedPlaceUseCaseImpl
import pt.socialfood.core.domain.usecase.theme.SetThemeModeUseCase
import pt.socialfood.core.domain.usecase.theme.SetThemeModeUseCaseImpl
import pt.socialfood.core.domain.usecase.user.ObserveUserUseCase
import pt.socialfood.core.domain.usecase.user.ObserveUserUseCaseImpl

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
