package pt.socialfood.presentation.guide.edit

import org.jetbrains.compose.resources.StringResource
import pt.socialfood.core.common.error.ErrorCode
import pt.socialfood.core.model.Guide
import pt.socialfood.core.model.GuideVisibility
import pt.socialfood.core.model.Restaurant

sealed interface EditGuideUiState {
    data object Loading : EditGuideUiState
    data class Loaded(
        val guide: Guide,
        val title: String,
        val description: String,
        val visibility: GuideVisibility,
        val titleError: Boolean = false,
        val descriptionError: Boolean = false,
        val restaurants: List<Restaurant>,
        val imageUrl: String? = null,
        val pendingImage: Pair<ByteArray, String>? = null,
        val isUploadingPhoto: Boolean = false,
        val isSaving: Boolean = false,
        val isDeleting: Boolean = false,
        val isAuthor: Boolean = false,
        val validationErrors: List<StringResource> = emptyList(),
    ) : EditGuideUiState
    data class Error(val errorCode: ErrorCode) : EditGuideUiState
}
