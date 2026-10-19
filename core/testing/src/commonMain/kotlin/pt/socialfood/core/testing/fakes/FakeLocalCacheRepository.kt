package pt.socialfood.core.testing.fakes

import pt.socialfood.core.domain.repository.LocalCacheRepository

class FakeLocalCacheRepository : LocalCacheRepository {
    var clearAllCallCount = 0
        private set

    override suspend fun clearAll() {
        clearAllCallCount++
    }
}
