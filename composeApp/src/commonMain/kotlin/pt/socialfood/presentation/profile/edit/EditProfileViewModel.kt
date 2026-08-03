package pt.socialfood.presentation.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.di.ImageCache
import pt.socialfood.domain.use_case.photo.UploadPhotoUseCase
import pt.socialfood.domain.use_case.user.GetPresignedUrlUseCase
import pt.socialfood.domain.use_case.user.GetUserMeUseCase
import pt.socialfood.domain.use_case.user.UpdateUserPhotoUseCase
import pt.socialfood.domain.use_case.user.UpdateUserUseCase
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

    private fun load() {
        viewModelScope.launch {
            _state.value = EditProfileUiState.Loading
            when (val result = getUserMe()) {
                is Result.Success -> {
                    val user = result.data
                    _state.value = EditProfileUiState.Loaded(
                        userId = user.id,
                        name = user.name,
                        username = user.username,
                        facebookUrl = user.facebookUrl.orEmpty(),
                        instagramUrl = user.instagramUrl.orEmpty(),
                        youtubeUrl = user.youtubeUrl.orEmpty(),
                        imageUrl = user.imageUrl,
                    )
                }

                is Result.Failure -> _state.value = EditProfileUiState.Error
            }
        }
    }

    fun retry() = load()

    fun onPhotoSelected(bytes: ByteArray, mimeType: String) {
        loaded { copy(pendingImage = Pair(bytes, mimeType)) }
    }

    fun dismissSaveError() = loaded { copy(saveError = false) }

    @OptIn(ExperimentalTime::class)
    fun save() {
        val state = _state.value as? EditProfileUiState.Loaded ?: return
        if (state.isSaving) return
        viewModelScope.launch {
            loaded { copy(isSaving = true) }

            if (state.pendingImage != null) {
                loaded { copy(isUploadingPhoto = true) }

                val (bytes, mimeType) = state.pendingImage
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
                        loaded { copy(isSaving = false, isUploadingPhoto = false, saveError = true) }
                        return@launch
                    }
                }

                if (uploadPhoto(presigned = presigned, bytes = bytes, mimeType = mimeType) is Result.Failure) {
                    loaded { copy(isSaving = false, isUploadingPhoto = false, saveError = true) }
                    return@launch
                }

                when (updateUserPhoto(id = state.userId, imageUrl = presigned.publicUrl)) {
                    is Result.Success -> {
                        imageCache.clear(presigned.publicUrl)
                        loaded {
                            copy(isUploadingPhoto = false, pendingImage = null, imageUrl = presigned.publicUrl)
                        }
                    }
                    is Result.Failure -> {
                        loaded { copy(isSaving = false, isUploadingPhoto = false, saveError = true) }
                        return@launch
                    }
                }
            }

            val current = _state.value as? EditProfileUiState.Loaded ?: return@launch
            when (
                updateUser(
                    id = current.userId,
                    name = current.name.ifBlank { null },
                    username = current.username.ifBlank { null },
                    facebookUrl = current.facebookUrl.ifBlank { null },
                    instagramUrl = current.instagramUrl.ifBlank { null },
                    youtubeUrl = current.youtubeUrl.ifBlank { null },
                )
            ) {
                is Result.Success -> loaded { copy(isSaving = false, saveSuccess = true) }
                is Result.Failure -> loaded { copy(isSaving = false, saveError = true) }
            }
        }
    }
}
