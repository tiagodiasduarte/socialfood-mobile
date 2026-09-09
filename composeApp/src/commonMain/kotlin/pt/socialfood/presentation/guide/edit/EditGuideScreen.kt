package pt.socialfood.presentation.guide.edit

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
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.TopTabs
import pt.socialfood.presentation.guide.GuideValidationErrorDialog
import pt.socialfood.presentation.guide.edit.card.GuideDetailsCard
import pt.socialfood.presentation.guide.edit.card.GuideRestaurantsCard
import pt.socialfood.presentation.guide.edit.card.GuideStatusCard
import pt.socialfood.presentation.imagepicker.rememberImagePickerLauncher
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.edit_guide_save_button
import socialfood.composeapp.generated.resources.edit_guide_tab_details
import socialfood.composeapp.generated.resources.edit_guide_tab_restaurants
import socialfood.composeapp.generated.resources.edit_guide_tab_status
import socialfood.composeapp.generated.resources.edit_guide_title

private const val TAB_DETAILS = 0
private const val TAB_RESTAURANTS = 1
private const val TAB_STATUS = 2

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
            .background(MaterialTheme.colorScheme.surface)
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

        Box(modifier = Modifier.weight(1f)) {
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
}

@Composable
private fun TopBar(isLoading: Boolean, onBackClick: () -> Unit, onSaveGuide: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
    onDeleteGuide: () -> Unit,
) {
    val pickImage = rememberImagePickerLauncher(onResult = onPhotoSelected)
    var selectedTab by rememberSaveable { mutableIntStateOf(initialTab) }
    val tabs = listOf(
        stringResource(Res.string.edit_guide_tab_details),
        stringResource(Res.string.edit_guide_tab_restaurants),
        stringResource(Res.string.edit_guide_tab_status),
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TopTabs(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            tabs = tabs,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.background),
        ) {
            when (selectedTab) {
                TAB_DETAILS -> GuideDetailsTab(
                    state = state,
                    onTitleChange = onTitleChange,
                    onDescriptionChange = onDescriptionChange,
                    onPickImage = pickImage,
                )

                TAB_RESTAURANTS -> GuideRestaurantsTab(
                    restaurants = state.restaurants,
                    onAddRestaurantsClick = onAddRestaurantsClick,
                    onRestaurantRemoved = onRestaurantRemoved,
                )

                TAB_STATUS -> GuideStatusTab(
                    state = state,
                    onVisibilityChange = onVisibilityChange,
                    onDeleteGuide = onDeleteGuide,
                )
            }
        }
    }
}

@Composable
private fun GuideDetailsTab(
    state: EditGuideUiState.Loaded,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPickImage: () -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
    ) {
        item {
            GuideDetailsCard(
                title = state.title,
                description = state.description,
                titleError = state.titleError,
                descriptionError = state.descriptionError,
                onTitleChange = onTitleChange,
                onDescriptionChange = onDescriptionChange,
                imageUrl = state.imageUrl,
                isUploadingPhoto = state.isUploadingPhoto,
                onPickImage = onPickImage,
                pendingImage = state.pendingImage,
            )
        }
    }
}

@Composable
private fun GuideRestaurantsTab(
    restaurants: List<Restaurant>,
    onAddRestaurantsClick: () -> Unit,
    onRestaurantRemoved: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
    ) {
        item {
            GuideRestaurantsCard(
                restaurants = restaurants,
                onAddClick = onAddRestaurantsClick,
                onRemoveClick = onRestaurantRemoved,
            )
        }
    }
}

@Composable
private fun GuideStatusTab(
    state: EditGuideUiState.Loaded,
    onVisibilityChange: (GuideVisibility) -> Unit,
    onDeleteGuide: () -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
    ) {
        item {
            GuideStatusCard(
                visibility = state.visibility,
                restaurantCount = state.restaurants.size,
                hasImage = state.imageUrl != null || state.pendingImage != null,
                isAuthor = state.isAuthor,
                onVisibilityChange = onVisibilityChange,
                onDeleteGuide = onDeleteGuide,
                shareCode = state.guide.shareCode,
            )
        }
    }
}

@Composable
@Preview
fun EditGuideScreenLoadingPreview() {
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

@Composable
@Preview
fun EditGuideScreenLoadedPreview() {
    val author = Author(id = "u1", name = "Sarah Mitchell", username = "sarahmitchell")
    val restaurant = Restaurant(
        id = "r1",
        name = "Le Jardin",
        description = "",
        city = "Downtown",
        country = "French",
        countryCode = "",
        postalCode = "",
        imagesUrl = emptyList(),
        address = "",
        rating = 4.8,
        userRatingCount = 320,
        websiteUrl = "",
        phoneNumber = "",
        location = Location(latitude = 48.8566, longitude = 2.3522),
    )

    val guide = Guide(
        id = "g1",
        name = "Michelin Star Favorites",
        description = "A carefully curated collection of the finest dining experiences in the city.",
        numberOfRestaurant = 1,
        visibility = GuideVisibility.PUBLIC,
        author = author,
        restaurants = listOf(restaurant),
    )

    AppTheme {
        EditGuideContent(
            state = EditGuideUiState.Loaded(
                guide = guide,
                title = guide.name,
                description = guide.description,
                visibility = guide.visibility,
                restaurants = guide.restaurants,
            ),
            onBackClick = {},
            onAddRestaurantsClick = {},
            onRestaurantRemoved = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onVisibilityChange = {},
            onRetry = {},
            onSaveGuide = {},
            onDeleteGuide = {},
        )
    }
}
