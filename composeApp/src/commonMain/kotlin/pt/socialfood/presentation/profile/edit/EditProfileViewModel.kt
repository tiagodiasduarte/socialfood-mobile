package pt.socialfood.presentation.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.di.ImageCache
import pt.socialfood.domain.usecase.photo.UploadPhotoUseCase
import pt.socialfood.domain.usecase.user.GetPresignedUrlUseCase
import pt.socialfood.domain.usecase.user.GetUserMeUseCase
import pt.socialfood.domain.usecase.user.UpdateUserPhotoUseCase
import pt.socialfood.domain.usecase.user.UpdateUserUseCase
import pt.socialfood.presentation.error.toErrorCode
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Suppress("TooManyFunctions")
class EditProfileViewModel(
    private val getUserMe: GetUserMeUseCase,
    private val updateUser: UpdateUserUseCase,
    private val uploadPhoto: UploadPhotoUseCase,
    private val updateUserPhoto: UpdateUserPhotoUseCase,
    private val getPresignedUrl: GetPresignedUrlUseCase,
    private val imageCache: ImageCache,
) : ViewModel() {

    private val _state = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Loading)
    val state: StateFlow<EditProfileUiState> = _state.asStateFlow()

    init {
        load()
    }

    private fun loaded(block: EditProfileUiState.Loaded.() -> EditProfileUiState.Loaded) {
        val current = _state.value as? EditProfileUiState.Loaded ?: return
        _state.value = current.block()
    }

    fun onNameChange(value: String) = loaded { copy(name = value) }
    fun onUsernameChange(value: String) = loaded { copy(username = value) }
    fun onFacebookUrlChange(value: String) = loaded { copy(facebookUrl = value) }
    fun onInstagramUrlChange(value: String) = loaded { copy(instagramUrl = value) }
    fun onYoutubeUrlChange(value: String) = loaded { copy(youtubeUrl = value) }
    fun onAuthorModeChange(value: Boolean) = loaded { copy(isAuthor = value) }

    private fun load() {
        viewModelScope.launch {
            _state.value = EditProfileUiState.Loading
            when (val result = getUserMe()) {
                is Result.Success -> {
                    val user = result.data
                    _state.value = EditProfileUiState.Loaded(
                        userId = user.id,
                        name = user.name,
                        email = user.email,
                        username = user.username,
                        facebookUrl = user.facebookUrl.orEmpty(),
                        instagramUrl = user.instagramUrl.orEmpty(),
                        youtubeUrl = user.youtubeUrl.orEmpty(),
                        imageUrl = user.imageUrl,
                        isAuthor = user.isAuthor,
                    )
                }

                is Result.Failure -> _state.value = EditProfileUiState.Error(result.error.toErrorCode())
            }
        }
    }

    fun retry() = load()

    fun onPhotoSelected(bytes: ByteArray, mimeType: String) {
        loaded { copy(pendingImage = Pair(bytes, mimeType)) }
    }

    fun dismissSaveError() = loaded { copy(saveError = null) }

    @Suppress("ReturnCount")
    @OptIn(ExperimentalTime::class)
    private suspend fun uploadPendingPhoto(state: EditProfileUiState.Loaded): Boolean {
        val pendingImage = state.pendingImage ?: return true
        loaded { copy(isUploadingPhoto = true) }

        val (bytes, mimeType) = pendingImage
        val ext = mimeType.substringAfter("/", "jpg").substringBefore(";")
        val fileName = "photo_${Clock.System.now().toEpochMilliseconds()}.$ext"

        val presigned = when (
            val result = getPresignedUrl(
                userId = state.userId,
                fileName = fileName,
                mimeType = mimeType,
                context = "profile",
            )
        ) {
            is Result.Success -> result.data
            is Result.Failure -> {
                loaded { copy(isSaving = false, isUploadingPhoto = false, saveError = result.error.toErrorCode()) }
                return false
            }
        }

        val uploadResult = uploadPhoto(presigned = presigned, bytes = bytes, mimeType = mimeType)
        if (uploadResult is Result.Failure) {
            loaded { copy(isSaving = false, isUploadingPhoto = false, saveError = uploadResult.error.toErrorCode()) }
            return false
        }

        return when (val photoResult = updateUserPhoto(id = state.userId, imageUrl = presigned.publicUrl)) {
            is Result.Success -> {
                imageCache.clear(presigned.publicUrl)
                loaded { copy(isUploadingPhoto = false, pendingImage = null, imageUrl = presigned.publicUrl) }
                true
            }
            is Result.Failure -> {
                loaded { copy(isSaving = false, isUploadingPhoto = false, saveError = photoResult.error.toErrorCode()) }
                false
            }
        }
    }

    fun save() {
        val state = _state.value as? EditProfileUiState.Loaded ?: return
        if (state.isSaving) return
        viewModelScope.launch {
            loaded { copy(isSaving = true) }

            if (!uploadPendingPhoto(state)) return@launch

            val current = _state.value as? EditProfileUiState.Loaded ?: return@launch
            when (
                val result = updateUser(
                    id = current.userId,
                    name = current.name.ifBlank { null },
                    username = current.username.ifBlank { null },
                    facebookUrl = current.facebookUrl,
                    instagramUrl = current.instagramUrl,
                    youtubeUrl = current.youtubeUrl,
                    isAuthor = current.isAuthor,
                )
            ) {
                is Result.Success -> loaded { copy(isSaving = false, saveSuccess = true) }
                is Result.Failure -> loaded { copy(isSaving = false, saveError = result.error.toErrorCode()) }
            }
        }
    }
}
