package pt.socialfood.fakes

import androidx.sqlite.SQLiteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.socialfood.data.local.dao.HomeDao
import pt.socialfood.data.local.entity.HomeSectionEntity

class FakeHomeDao(private val shouldThrowOnWrite: Boolean = false) : HomeDao {

    private val entities = LinkedHashMap<String, HomeSectionEntity>()
    private val activeFlow = MutableStateFlow<List<HomeSectionEntity>>(emptyList())

    override suspend fun upsertAll(sections: List<HomeSectionEntity>) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        sections.forEach { entities[it.id] = it }
        refreshActiveFlow()
    }

    override suspend fun deleteAll() {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        entities.clear()
        refreshActiveFlow()
    }

    override fun observeActive(): Flow<List<HomeSectionEntity>> = activeFlow

    override suspend fun getAllActive(): List<HomeSectionEntity> =
        entities.values.filter { it.isActive }.sortedBy { it.position }

    private fun refreshActiveFlow() {
        activeFlow.value = entities.values.filter { it.isActive }.sortedBy { it.position }
    }
}
