package pt.socialfood.presentation.profile.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.presentation.components.ErrorAlertDialog
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.error.stringResource
import pt.socialfood.presentation.profile.edit.card.PersonalDetailsCard
import pt.socialfood.presentation.profile.edit.card.ProfilePictureCard
import pt.socialfood.presentation.profile.edit.card.SocialNetworkCard
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.back_button_description
import socialfood.composeapp.generated.resources.edit_profile_save_button
import socialfood.composeapp.generated.resources.edit_profile_save_error_dismiss
import socialfood.composeapp.generated.resources.edit_profile_save_error_title
import socialfood.composeapp.generated.resources.edit_profile_title

@Composable
fun EditProfileScreen(onBackClick: () -> Unit, viewModel: EditProfileViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state is EditProfileUiState.Loaded) {
        LaunchedEffect((state as EditProfileUiState.Loaded).saveSuccess) {
            if ((state as EditProfileUiState.Loaded).saveSuccess) onBackClick()
        }
    }

    when (val s = state) {
        is EditProfileUiState.Loading -> EditProfilePlaceholder()
        is EditProfileUiState.Error -> Column(
            modifier = Modifier.fillMaxSize().background(GreyBackground),
        ) {
            TopBar(isSaving = false, showSaveButton = false, onBackClick = onBackClick, onSaveClick = {})
            ErrorContent(modifier = Modifier.fillMaxSize(), onRetryClick = viewModel::retry)
        }

        is EditProfileUiState.Loaded -> EditProfileContent(
            state = s,
            onBackClick = onBackClick,
            onSaveClick = viewModel::save,
            onPhotoSelected = viewModel::onPhotoSelected,
            onNameChange = viewModel::onNameChange,
            onUsernameChange = viewModel::onUsernameChange,
            onFacebookUrlChange = viewModel::onFacebookUrlChange,
            onInstagramUrlChange = viewModel::onInstagramUrlChange,
            onYoutubeUrlChange = viewModel::onYoutubeUrlChange,
            onDismissSaveError = viewModel::dismissSaveError,
        )
    }
}

@Composable
private fun EditProfileContent(
    state: EditProfileUiState.Loaded,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onPhotoSelected: (ByteArray, String) -> Unit,
    onNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onFacebookUrlChange: (String) -> Unit,
    onInstagramUrlChange: (String) -> Unit,
    onYoutubeUrlChange: (String) -> Unit,
    onDismissSaveError: () -> Unit,
) {
    val saveError = state.saveError
    if (saveError != null) {
        SaveErrorDialog(errorCode = saveError, onDismiss = onDismissSaveError)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreyBackground),
    ) {
        TopBar(
            isSaving = state.isSaving,
            showSaveButton = true,
            onBackClick = onBackClick,
            onSaveClick = onSaveClick,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(SpaceSize.large),
            verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            ProfilePictureCard(state = state, onPhotoSelected = onPhotoSelected)

            PersonalDetailsCard(
                state = state,
                onNameChange = onNameChange,
                onUsernameChange = onUsernameChange,
            )

            SocialNetworkCard(
                state = state,
                onFacebookUrlChange = onFacebookUrlChange,
                onInstagramUrlChange = onInstagramUrlChange,
                onYoutubeUrlChange = onYoutubeUrlChange,
            )
        }
    }
}

@Composable
private fun SaveErrorDialog(errorCode: ErrorCode, onDismiss: () -> Unit) {
    ErrorAlertDialog(
        title = stringResource(Res.string.edit_profile_save_error_title),
        message = stringResource(errorCode.stringResource()),
        confirmButtonText = stringResource(Res.string.edit_profile_save_error_dismiss),
        onDismiss = onDismiss,
    )
}

@Composable
private fun TopBar(isSaving: Boolean, showSaveButton: Boolean, onBackClick: () -> Unit, onSaveClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = SpaceSize.medium, vertical = SpaceSize.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(Res.string.back_button_description),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
        Text(
            text = stringResource(Res.string.edit_profile_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
        )
        if (showSaveButton) {
            Button(
                onClick = onSaveClick,
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(SpaceSize.large),
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(SpaceSize.large),
                        strokeWidth = 2.dp,
                        color = Color.White,
                    )
                } else {
                    Text(
                        stringResource(Res.string.edit_profile_save_button),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        } else {
            Box(modifier = Modifier.size(48.dp))
        }
    }
}

@Preview
@Composable
private fun EditProfileScreenPreview() {
    AppTheme {
        EditProfileContent(
            state = EditProfileUiState.Loaded(
                name = "John Doe",
                email = "john.doe@email.com",
                username = "johndoe",
                facebookUrl = "",
                instagramUrl = "",
                youtubeUrl = "",
                isGoogleConnected = true,
            ),
            onBackClick = {},
            onSaveClick = {},
            onPhotoSelected = { _, _ -> },
            onNameChange = {},
            onUsernameChange = {},
            onFacebookUrlChange = {},
            onInstagramUrlChange = {},
            onYoutubeUrlChange = {},
            onDismissSaveError = {},
        )
    }
}
