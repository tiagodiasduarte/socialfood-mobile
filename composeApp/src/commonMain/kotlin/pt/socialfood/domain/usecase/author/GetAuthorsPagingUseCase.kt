package pt.socialfood.domain.usecase.author

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.Author

interface GetAuthorsPagingUseCase {
    operator fun invoke(): Flow<PagingData<Author>>
}
