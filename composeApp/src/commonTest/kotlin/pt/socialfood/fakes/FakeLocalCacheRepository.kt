package pt.socialfood.fakes

import pt.socialfood.domain.repository.LocalCacheRepository

class FakeLocalCacheRepository : LocalCacheRepository {
    var clearAllCallCount = 0
        private set

    override suspend fun clearAll() {
        clearAllCallCount++
    }
}
