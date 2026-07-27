package pt.socialfood.fakes

import kotlinx.coroutines.CompletableDeferred
import pt.socialfood.core.Result
import pt.socialfood.domain.model.User
import pt.socialfood.domain.use_case.user.GetUserMeUseCase

/**
 * A [GetUserMeUseCase] fake whose [invoke] suspends until [resolve] is called, letting tests
 * control exactly when the underlying network call "completes" — used to exercise races between
 * tab selection and `getUserMe()` resolution.
 */
class FakeDelayedGetUserMeUseCase(
    private val result: Result<User>,
) : GetUserMeUseCase {
    private val gate = CompletableDeferred<Unit>()

    var invokeCount: Int = 0
        private set

    fun resolve() {
        gate.complete(Unit)
    }

    override suspend fun invoke(): Result<User> {
        invokeCount++
        gate.await()
        return result
    }
}
