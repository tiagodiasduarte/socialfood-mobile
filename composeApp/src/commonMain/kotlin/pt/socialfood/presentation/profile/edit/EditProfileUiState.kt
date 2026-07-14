package pt.socialfood.presentation.profile.edit

sealed interface EditProfileUiState {
    data object Loading : EditProfileUiState
    data class Loaded(
        val isSaving: Boolean = false,
        val saveSuccess: Boolean = false,
        val isUploadingPhoto: Boolean = false,
        val userId: String = "",
        val username: String = "",
        val role: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val phoneNumber: String = "",
        val city: String = "",
        val country: String = "",
        val bio: String = "",
        val facebookUrl: String = "",
        val instagramUrl: String = "",
        val youtubeUrl: String = "",
        val imageUrl: String? = null,
        val pendingImage: Pair<ByteArray, String>? = null,
        val isGoogleConnected: Boolean = false,
    ) : EditProfileUiState
    data object Error : EditProfileUiState
}
