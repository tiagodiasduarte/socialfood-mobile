package pt.socialfood.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.socialfood.core.domain.usecase.user.ObserveUserUseCase
import pt.socialfood.core.model.User

class FakeObserveUserUseCase(initial: User? = null) : ObserveUserUseCase {
    private val user = MutableStateFlow(initial)

    fun emit(user: User?) {
        this.user.value = user
    }

    override operator fun invoke(): Flow<User?> = user
}
