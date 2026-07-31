package pt.socialfood.domain.use_case.user

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PresignedUrlData

interface GetPresignedUrlUseCase {
    suspend operator fun invoke(
        userId: String,
        fileName: String,
        mimeType: String,
        context: String
    ): Result<PresignedUrlData>
}
