package pt.socialfood.presentation.guide.create

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pt.socialfood.presentation.components.TopBar
import pt.socialfood.presentation.guide.GuideValidationErrorDialog
import pt.socialfood.presentation.guide.edit.card.GuideDetailsCard
import pt.socialfood.presentation.imagepicker.rememberImagePickerLauncher
import pt.socialfood.ui.theme.AppTheme
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
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
    ) {
        TopBar(
            title = stringResource(Res.string.create_guide_create_title),
            onBackClick = onBackClick,
            isActionLoading = state is CreateGuideUiState.Loading,
            actionButtonText = stringResource(Res.string.create_guide_save_button),
            onActionClick = onCreateGuide,
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
