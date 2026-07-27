package pt.socialfood.presentation.authors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.use_case.author.GetAuthorsPagingUseCase

class AuthorsViewModel(
    private val getAuthorsPaging: GetAuthorsPagingUseCase,
) : ViewModel() {

    val authors: Flow<PagingData<Author>> = getAuthorsPaging().cachedIn(viewModelScope)
}
