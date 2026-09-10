package pt.socialfood.presentation.drawer

import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.User

sealed interface DrawerUiState {
    data object Loading : DrawerUiState
    data class Loaded(val user: User) : DrawerUiState
    data class Error(val errorCode: ErrorCode) : DrawerUiState
    data object LoggedOut : DrawerUiState
}
