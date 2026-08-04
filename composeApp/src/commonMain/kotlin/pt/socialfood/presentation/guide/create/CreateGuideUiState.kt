package pt.socialfood.presentation.guide.create

import org.jetbrains.compose.resources.StringResource
import pt.socialfood.domain.error.ErrorCode

sealed interface CreateGuideUiState {
    data class Idle(
        val title: String = "",
        val description: String = "",
        val titleError: Boolean = false,
        val descriptionError: Boolean = false,
        val pendingImage: Pair<ByteArray, String>? = null,
        val validationErrors: List<StringResource> = emptyList(),
    ) : CreateGuideUiState

    data object Loading : CreateGuideUiState

    data class Error(val errorCode: ErrorCode) : CreateGuideUiState
}
