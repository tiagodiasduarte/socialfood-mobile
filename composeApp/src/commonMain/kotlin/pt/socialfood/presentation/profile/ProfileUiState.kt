package pt.socialfood.presentation.profile

import pt.socialfood.domain.model.User

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Loaded(val user: User) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
    data object LoggedOut : ProfileUiState
}
