package pt.socialfood.presentation.author.detail

import pt.socialfood.domain.model.AuthorDetail

sealed interface AuthorDetailUiState {
    data object Loading : AuthorDetailUiState
    data class Loaded(val author: AuthorDetail) : AuthorDetailUiState
    data object Error : AuthorDetailUiState
}
