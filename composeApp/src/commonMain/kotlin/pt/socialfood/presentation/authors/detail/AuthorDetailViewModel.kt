package pt.socialfood.presentation.authors.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.author.GetAuthorByIdUseCase

class AuthorDetailViewModel(
    private val getAuthorById: GetAuthorByIdUseCase,
    private val authorId: String,
) : ViewModel() {

    private val _state = MutableStateFlow<AuthorDetailUiState>(AuthorDetailUiState.Loading)
    val state: StateFlow<AuthorDetailUiState> = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = AuthorDetailUiState.Loading
            _state.value = when (val result = getAuthorById(authorId)) {
                is Result.Success -> AuthorDetailUiState.Loaded(result.data)
                is Result.Error -> AuthorDetailUiState.Error
            }
        }
    }
}
