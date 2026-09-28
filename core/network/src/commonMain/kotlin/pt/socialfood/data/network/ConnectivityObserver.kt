package pt.socialfood.data.network

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val isOnline: Flow<Boolean>
}
