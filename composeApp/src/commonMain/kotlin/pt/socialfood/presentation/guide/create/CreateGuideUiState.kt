package pt.socialfood.presentation.guide.create

import pt.socialfood.domain.error.ErrorEntity

sealed interface CreateGuideUiState {
    data class Idle(
        val title: String = "",
        val description: String = "",
        val titleError: Boolean = false,
        val descriptionError: Boolean = false,
        val pendingImage: Pair<ByteArray, String>? = null,
        val validationErrors: List<ErrorEntity.Validation> = emptyList(),
    ) : CreateGuideUiState

    data object Loading : CreateGuideUiState

    data class Error(val message: String) : CreateGuideUiState
}
