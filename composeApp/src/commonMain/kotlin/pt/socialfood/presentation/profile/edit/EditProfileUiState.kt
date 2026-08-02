package pt.socialfood.presentation.profile.edit

sealed interface EditProfileUiState {
    data object Loading : EditProfileUiState
    data class Loaded(
        val isSaving: Boolean = false,
        val saveSuccess: Boolean = false,
        val saveError: Boolean = false,
        val isUploadingPhoto: Boolean = false,
        val userId: String = "",
        val name: String = "",
        val username: String = "",
        val facebookUrl: String = "",
        val instagramUrl: String = "",
        val youtubeUrl: String = "",
        val imageUrl: String? = null,
        val pendingImage: Pair<ByteArray, String>? = null,
        val isGoogleConnected: Boolean = false,
    ) : EditProfileUiState
    data object Error : EditProfileUiState
}
