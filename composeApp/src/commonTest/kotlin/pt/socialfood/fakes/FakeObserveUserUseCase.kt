package pt.socialfood.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.socialfood.domain.model.User
import pt.socialfood.domain.use_case.user.ObserveUserUseCase

class FakeObserveUserUseCase(
    initial: User? = null,
) : ObserveUserUseCase {
    private val user = MutableStateFlow(initial)

    fun emit(user: User?) {
        this.user.value = user
    }

    override operator fun invoke(): Flow<User?> = user
}
