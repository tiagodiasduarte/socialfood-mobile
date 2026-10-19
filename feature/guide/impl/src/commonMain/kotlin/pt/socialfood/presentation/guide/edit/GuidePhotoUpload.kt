package pt.socialfood.presentation.guide.edit

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.GuidesRepository
import pt.socialfood.domain.repository.PhotosRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Uploads [bytes] as the cover photo of guide [guideId] and returns its public URL, or `null` if
 * any step failed.
 */
@OptIn(ExperimentalTime::class)
internal suspend fun uploadGuidePhoto(
    guidesRepository: GuidesRepository,
    photosRepository: PhotosRepository,
    guideId: String,
    bytes: ByteArray,
    mimeType: String,
): String? {
    val ext =
        when (mimeType) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> "jpg"
        }
    val fileName = "photo_${Clock.System.now().toEpochMilliseconds()}.$ext"
    val presigned = guidesRepository.getPhotoPresignedUrl(guideId, fileName, mimeType) as? Result.Success
        ?: return null
    val publicUrl = presigned.data.publicUrl
    val uploaded =
        photosRepository.uploadToS3(presigned.data.uploadUrl, bytes, mimeType) is Result.Success &&
            guidesRepository.addPhoto(guideId, publicUrl) is Result.Success
    return if (uploaded) publicUrl else null
}
