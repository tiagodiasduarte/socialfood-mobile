package pt.socialfood.presentation.guide.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import org.koin.core.parameter.parametersOf
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.edit_guide_delete_button
import socialfood.composeapp.generated.resources.edit_guide_save_button
import socialfood.composeapp.generated.resources.edit_guide_tab_details
import socialfood.composeapp.generated.resources.edit_guide_tab_restaurants
import socialfood.composeapp.generated.resources.edit_guide_tab_status
import socialfood.composeapp.generated.resources.edit_guide_title
import pt.socialfood.presentation.guide.GuideValidationErrorDialog
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.TopTabs
import pt.socialfood.presentation.components.buttons.IconTextButton
import pt.socialfood.presentation.guide.edit.card.GuideDetailsCard
import pt.socialfood.presentation.guide.edit.card.GuideRestaurantsCard
import pt.socialfood.presentation.guide.edit.card.GuideStatusCard
import pt.socialfood.presentation.imagepicker.rememberImagePickerLauncher
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize

private const val TAB_DETAILS = 0
private const val TAB_RESTAURANTS = 1
private const val TAB_STATUS = 2

@Suppress("LongParameterList")
@Composable
fun EditGuideScreen(
    guideId: String,
    onBackClick: () -> Unit,
    onGuideDeleted: () -> Unit = {},
    initialTab: Int = TAB_DETAILS,
    onAddRestaurantsClick: (onRestaurantAdded: (Restaurant) -> Unit) -> Unit = {},
    viewModel: EditGuideViewModel = koinViewModel(parameters = { parametersOf(guideId) }),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                EditGuideViewModel.UiEvent.NavigateBack -> onBackClick()
                EditGuideViewModel.UiEvent.GuideDeleted -> onGuideDeleted()
            }
        }
    }

    EditGuideContent(
        state = state,
        initialTab = initialTab,
        onBackClick = onBackClick,
        onAddRestaurantsClick = { onAddRestaurantsClick(viewModel::onRestaurantAdded) },
        onRestaurantRemoved = viewModel::onRestaurantRemoved,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onVisibilityChange = viewModel::onVisibilityChange,
        onRetry = viewModel::onRetry,
        onSaveGuide = viewModel::onSave,
        onPhotoSelected = viewModel::onPhotoSelected,
        onDeleteGuide = viewModel::onDelete,
        onDismissErrors = viewModel::onDismissErrors,
    )
}

@Composable
private fun EditGuideContent(
    state: EditGuideUiState,
    initialTab: Int = TAB_DETAILS,
    onBackClick: () -> Unit,
    onAddRestaurantsClick: () -> Unit,
    onRestaurantRemoved: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onVisibilityChange: (GuideVisibility) -> Unit = {},
    onRetry: () -> Unit,
    onSaveGuide: () -> Unit,
    onPhotoSelected: (ByteArray, String) -> Unit = { _, _ -> },
    onDeleteGuide: () -> Unit = {},
    onDismissErrors: () -> Unit = {},
) {
    val validationErrors = (state as? EditGuideUiState.Loaded)?.validationErrors.orEmpty()
    if (validationErrors.isNotEmpty()) {
        GuideValidationErrorDialog(
            errors = validationErrors,
            onDismiss = onDismissErrors,
        )
    }

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
    ) {
        TopBar(
            isLoading = (state as? EditGuideUiState.Loaded)?.let { it.isSaving || it.isUploadingPhoto || it.isDeleting }
                ?: (state is EditGuideUiState.Loading),
            onBackClick = onBackClick,
            onSaveGuide = onSaveGuide,
        )

        when (state) {
            is EditGuideUiState.Error -> {
                ErrorContent(modifier = Modifier.fillMaxSize(), onRetryClick = onRetry)
            }

            is EditGuideUiState.Loaded -> {
                GuideLoaded(
                    state = state,
                    initialTab = initialTab,
                    onTitleChange = onTitleChange,
                    onDescriptionChange = onDescriptionChange,
                    onVisibilityChange = onVisibilityChange,
                    onAddRestaurantsClick = onAddRestaurantsClick,
                    onRestaurantRemoved = onRestaurantRemoved,
                    onPhotoSelected = onPhotoSelected,
                    onDeleteGuide = onDeleteGuide,
                )
            }

            EditGuideUiState.Loading -> {
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
private fun TopBar(
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onSaveGuide: () -> Unit,
) {
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
            text = stringResource(Res.string.edit_guide_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Button(
            onClick = onSaveGuide,
            enabled = !isLoading,
            shape = RoundedCornerShape(SpaceSize.medium),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            contentPadding = PaddingValues(
                horizontal = SpaceSize.large,
                vertical = SpaceSize.medium
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
                    text = stringResource(Res.string.edit_guide_save_button),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun GuideLoaded(
    state: EditGuideUiState.Loaded,
    initialTab: Int = TAB_DETAILS,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onVisibilityChange: (GuideVisibility) -> Unit,
    onAddRestaurantsClick: () -> Unit,
    onRestaurantRemoved: (String) -> Unit,
    onPhotoSelected: (ByteArray, String) -> Unit,
    onDeleteGuide: () -> Unit = {},
) {
    val pickImage = rememberImagePickerLauncher(onResult = onPhotoSelected)
    var selectedTab by rememberSaveable { mutableIntStateOf(initialTab) }
    val tabs = listOf(
        stringResource(Res.string.edit_guide_tab_details),
        stringResource(Res.string.edit_guide_tab_restaurants),
        stringResource(Res.string.edit_guide_tab_status),
    )

    TopTabs(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        tabs = tabs,
    )

    when (selectedTab) {
        TAB_DETAILS -> LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(GreyBackground),
            contentPadding = PaddingValues(SpaceSize.large),
            verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            item {
                GuideDetailsCard(
                    modifier = Modifier.fillMaxSize(),
                    title = state.title,
                    description = state.description,
                    titleError = state.titleError,
                    descriptionError = state.descriptionError,
                    onTitleChange = onTitleChange,
                    onDescriptionChange = onDescriptionChange,
                    imageUrl = state.imageUrl,
                    isUploadingPhoto = state.isUploadingPhoto,
                    onPickImage = pickImage,
                    pendingImage = state.pendingImage,
                )
            }
        }

        TAB_RESTAURANTS -> LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(GreyBackground),
            contentPadding = PaddingValues(SpaceSize.large),
            verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            item {
                GuideRestaurantsCard(
                    restaurants = state.restaurants,
                    onAddClick = onAddRestaurantsClick,
                    onRemoveClick = onRestaurantRemoved,
                )
            }
        }

        TAB_STATUS -> LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(GreyBackground),
            contentPadding = PaddingValues(SpaceSize.large),
            verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            item {
                GuideStatusCard(
                    visibility = state.visibility,
                    restaurantCount = state.restaurants.size,
                    hasImage = state.imageUrl != null || state.pendingImage != null,
                    onVisibilityChange = onVisibilityChange,
                )
            }
            item {
                IconTextButton(
                    text = stringResource(Res.string.edit_guide_delete_button),
                    icon = Icons.Outlined.Delete,
                    onClick = onDeleteGuide,
                )
            }
        }
    }
}


@Composable
@Preview
fun EditGuideScreenPreview() {
    AppTheme {
        EditGuideContent(
            state = EditGuideUiState.Loading,
            onBackClick = {},
            onAddRestaurantsClick = {},
            onRestaurantRemoved = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onRetry = {},
            onSaveGuide = {},
        )
    }
}
