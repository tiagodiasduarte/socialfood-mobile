package pt.socialfood.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.PresignedUrlData

@Suppress("TooManyFunctions")
interface GuidesRepository {
    suspend fun addPhoto(guideId: String, imageUrl: String): Result<Boolean>

    suspend fun addRestaurantGuide(guideId: String, userId: String, placeId: String?): Result<Guide>

    suspend fun create(name: String, description: String, userId: String): Result<Guide>

    suspend fun delete(id: String): Result<Boolean>

    suspend fun deletePhoto(guideId: String): Result<Boolean>

    suspend fun findById(id: String): Result<Guide>

    suspend fun findGuideBySharedCode(code: String): Result<Guide>

    fun findGuidesPagingFlow(): Flow<PagingData<Guide>>

    fun findUserGuidesPagingFlow(userId: String): Flow<PagingData<Guide>>

    fun findUserJoinedGuidesPagingFlow(userId: String): Flow<PagingData<Guide>>

    suspend fun getPhotoPresignedUrl(guideId: String, fileName: String, mimeType: String): Result<PresignedUrlData>

    suspend fun update(
        id: String,
        name: String,
        userId: String,
        description: String,
        restaurantIds: List<String>,
        visibility: GuideVisibility,
    ): Result<Guide>
}
