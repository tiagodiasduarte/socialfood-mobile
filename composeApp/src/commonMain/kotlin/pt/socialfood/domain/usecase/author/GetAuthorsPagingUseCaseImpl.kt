package pt.socialfood.domain.usecase.author

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.repository.AuthorsRepository

class GetAuthorsPagingUseCaseImpl(
    private val repository: AuthorsRepository,
) : GetAuthorsPagingUseCase {
    override operator fun invoke(): Flow<PagingData<Author>> = repository.getAuthorsPagingFlow()
}
