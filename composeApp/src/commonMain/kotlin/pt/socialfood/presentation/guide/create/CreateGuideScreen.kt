package pt.socialfood.presentation.guide.create

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pt.socialfood.presentation.guide.GuideValidationErrorDialog
import pt.socialfood.presentation.guide.edit.card.GuideDetailsCard
import pt.socialfood.presentation.imagepicker.rememberImagePickerLauncher
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.create_guide_create_title
import socialfood.composeapp.generated.resources.create_guide_save_button

@Composable
fun CreateGuideScreen(
    onBackClick: () -> Unit,
    onGuideCreated: (String) -> Unit,
    viewModel: CreateGuideViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CreateGuideViewModel.UiEvent.GuideCreated -> onGuideCreated(event.guideId)
            }
        }
    }

    CreateGuideContent(
        state = state,
        onBackClick = onBackClick,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onCreateGuide = viewModel::onCreateGuide,
        onPhotoSelected = viewModel::onPhotoSelected,
        onDismissErrors = viewModel::onDismissErrors,
    )
}

@Composable
private fun CreateGuideContent(
    state: CreateGuideUiState,
    onBackClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCreateGuide: () -> Unit,
    onPhotoSelected: (ByteArray, String) -> Unit = { _, _ -> },
    onDismissErrors: () -> Unit = {},
) {
    val pickImage = rememberImagePickerLauncher(onResult = onPhotoSelected)
    val focusManager = LocalFocusManager.current

    val validationErrors = (state as? CreateGuideUiState.Idle)?.validationErrors.orEmpty()
    if (validationErrors.isNotEmpty()) {
        GuideValidationErrorDialog(
            errors = validationErrors,
            onDismiss = onDismissErrors,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreyBackground)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
    ) {
        TopBar(
            isLoading = state is CreateGuideUiState.Loading,
            onBackClick = onBackClick,
            onCreateGuide = onCreateGuide,
        )

        when (state) {
            is CreateGuideUiState.Error -> {}
            is CreateGuideUiState.Idle -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(SpaceSize.large),
                    verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
                ) {
                    item {
                        GuideDetailsCard(
                            modifier = Modifier.fillParentMaxSize(),
                            title = state.title,
                            description = state.description,
                            titleError = state.titleError,
                            descriptionError = state.descriptionError,
                            onTitleChange = onTitleChange,
                            onDescriptionChange = onDescriptionChange,
                            onPickImage = pickImage,
                            pendingImage = state.pendingImage,
                        )
                    }
                }
            }

            CreateGuideUiState.Loading -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun TopBar(isLoading: Boolean, onBackClick: () -> Unit, onCreateGuide: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = SpaceSize.medium, vertical = SpaceSize.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }

        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(Res.string.create_guide_create_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Button(
            onClick = onCreateGuide,
            enabled = !isLoading,
            shape = RoundedCornerShape(SpaceSize.medium),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            contentPadding = PaddingValues(
                horizontal = SpaceSize.large,
                vertical = SpaceSize.medium,
            ),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text(
                    text = stringResource(Res.string.create_guide_save_button),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
@Preview
fun CreateGuideScreenPreview() {
    AppTheme {
        CreateGuideContent(
            state = CreateGuideUiState.Idle(title = "My Guide"),
            onBackClick = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onCreateGuide = {},
        )
    }
}
