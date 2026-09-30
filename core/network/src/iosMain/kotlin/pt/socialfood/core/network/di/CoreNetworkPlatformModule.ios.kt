package pt.socialfood.core.network.di

import org.koin.core.module.Module
import org.koin.dsl.module
import pt.socialfood.data.network.ConnectivityObserver
import pt.socialfood.data.network.ConnectivityObserverImpl

internal actual val coreNetworkPlatformModule: Module = module {
    single<ConnectivityObserver> { ConnectivityObserverImpl() }
}
