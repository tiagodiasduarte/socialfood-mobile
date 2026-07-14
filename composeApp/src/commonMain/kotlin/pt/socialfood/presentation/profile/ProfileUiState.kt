package pt.socialfood.presentation.profile

import pt.socialfood.domain.model.User

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Loaded(val user: User) : ProfileUiState
    data object Error : ProfileUiState
    data object LoggedOut : ProfileUiState
}
