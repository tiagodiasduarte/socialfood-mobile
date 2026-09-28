package pt.socialfood.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.socialfood.data.network.ConnectivityObserver

class FakeConnectivityObserver(initiallyOnline: Boolean = true) : ConnectivityObserver {

    private val _isOnline = MutableStateFlow(initiallyOnline)
    override val isOnline: Flow<Boolean> = _isOnline

    fun setOnline(online: Boolean) {
        _isOnline.value = online
    }
}
