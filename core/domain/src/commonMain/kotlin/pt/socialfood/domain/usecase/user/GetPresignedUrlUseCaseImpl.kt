package pt.socialfood.domain.usecase.user

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.repository.UsersRepository

class GetPresignedUrlUseCaseImpl(private val repository: UsersRepository) : GetPresignedUrlUseCase {
    override suspend operator fun invoke(
        userId: String,
        fileName: String,
        mimeType: String,
        context: String,
    ): Result<PresignedUrlData> = repository.getPresignedUrl(
        userId = userId,
        fileName = fileName,
        mimeType = mimeType,
        context = context,
    )
}
