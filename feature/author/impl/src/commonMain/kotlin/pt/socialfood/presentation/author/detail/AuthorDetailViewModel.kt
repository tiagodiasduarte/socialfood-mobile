package pt.socialfood.presentation.author.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.repository.AuthorsRepository
import pt.socialfood.core.ui.error.toErrorCode

class AuthorDetailViewModel(private val authorsRepository: AuthorsRepository, private val authorId: String) :
    ViewModel() {

    private val _state = MutableStateFlow<AuthorDetailUiState>(AuthorDetailUiState.Loading)
    val state: StateFlow<AuthorDetailUiState> = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = AuthorDetailUiState.Loading
            _state.value = when (val result = authorsRepository.findAuthorById(authorId)) {
                is Result.Success -> AuthorDetailUiState.Loaded(result.data)
                is Result.Failure -> AuthorDetailUiState.Error(result.error.toErrorCode())
            }
        }
    }
}
