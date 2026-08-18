package pt.socialfood.presentation.restaurant.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.buttons.ActionButton
import pt.socialfood.presentation.components.detailImageScrim
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.FavouriteRed
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.ImagePlaceholderColor
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.back_button_description
import socialfood.composeapp.generated.resources.restaurant_detail_add_to_wishlist_button
import socialfood.composeapp.generated.resources.restaurant_detail_favourite_description
import socialfood.composeapp.generated.resources.restaurant_detail_more_options_description
import socialfood.composeapp.generated.resources.restaurant_detail_move_to_visited_button
import socialfood.composeapp.generated.resources.restaurant_detail_opening_hours_title
import socialfood.composeapp.generated.resources.restaurant_detail_share_button

val ImageHeight = 300.dp
private const val GALLERY_PHOTO_COUNT = 5

@Composable
fun RestaurantDetailScreen(
    restaurantId: String,
    onBackClick: () -> Unit,
    viewModel: RestaurantDetailViewModel = koinViewModel { parametersOf(restaurantId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RestaurantDetailContent(
        state = state,
        onBackClick = onBackClick,
        onRetry = viewModel::load,
        onToggleFavourite = viewModel::toggleFavourite,
        onAddToWishlist = viewModel::addToWishlist,
        onMoveToVisited = viewModel::moveToVisited,
    )
}

@Composable
private fun RestaurantDetailContent(
    state: RestaurantDetailUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onToggleFavourite: () -> Unit = {},
    onAddToWishlist: () -> Unit = {},
    onMoveToVisited: () -> Unit = {},
) {
    when (state) {
        RestaurantDetailUiState.Loading -> RestaurantDetailSkeleton()

        is RestaurantDetailUiState.Loaded -> RestaurantDetailLoaded(
            restaurant = state.restaurant,
            isFavourite = state.isFavourite,
            visitStatus = state.visitStatus,
            onBackClick = onBackClick,
            onToggleFavourite = onToggleFavourite,
            onAddToWishlist = onAddToWishlist,
            onMoveToVisited = onMoveToVisited,
        )

        is RestaurantDetailUiState.Error -> RestaurantDetailError(onBackClick = onBackClick, onRetry = onRetry)
    }
}

@Composable
private fun RestaurantDetailError(onBackClick: () -> Unit, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(GreyBackground)) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.padding(SpaceSize.medium),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(Res.string.back_button_description),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }

        ErrorContent(
            modifier = Modifier.fillMaxSize(),
            onRetryClick = onRetry,
        )
    }
}

@Composable
private fun RestaurantDetailLoaded(
    restaurant: Restaurant,
    isFavourite: Boolean,
    visitStatus: VisitStatus?,
    onBackClick: () -> Unit,
    onToggleFavourite: () -> Unit = {},
    onAddToWishlist: () -> Unit = {},
    onMoveToVisited: () -> Unit = {},
) {
    val uriHandler = LocalUriHandler.current

    Box(modifier = Modifier.fillMaxSize().background(GreyBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            TopSection(
                restaurant = restaurant,
                isFavourite = isFavourite,
                visitStatus = visitStatus,
                onBackClick = onBackClick,
                onShareClick = {},
                onFavoriteClick = onToggleFavourite,
                onAddToWishlistClick = onAddToWishlist,
                onMoveToVisitedClick = onMoveToVisited,
            )

            TitleSection(restaurant = restaurant)

            PhotoGallerySection(restaurant)

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = SpaceSize.large),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            Spacer(Modifier.height(SpaceSize.large))

            InformationSection(
                restaurant = restaurant,
                onNavigateClick = {
                    if (restaurant.address.isNotBlank()) {
                        uriHandler.openUri("geo:0,0?q=${restaurant.address}")
                    }
                },
                onWebsiteClick = {
                    if (!restaurant.websiteUrl.isNullOrBlank()) {
                        uriHandler.openUri(restaurant.websiteUrl)
                    }
                },
            )

            Spacer(Modifier.height(SpaceSize.xlarge))

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = SpaceSize.large),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            Spacer(Modifier.height(SpaceSize.xlarge))

            OpeningHoursSection(restaurant)

            Spacer(Modifier.height(88.dp))
        }

        CallButton(
            onClick = {
                if (restaurant.phoneNumber.isNotBlank()) {
                    uriHandler.openUri("tel:${restaurant.phoneNumber}")
                }
            },
        )
    }
}

@Suppress("LongMethod")
@Composable
private fun TopSection(
    restaurant: Restaurant,
    isFavourite: Boolean,
    visitStatus: VisitStatus?,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAddToWishlistClick: () -> Unit,
    onMoveToVisitedClick: () -> Unit,
) {
    val imageUrl = restaurant.photoNames.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ImageHeight),
    ) {
        if (imageUrl != null) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = restaurant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    Box(Modifier.fillMaxSize().background(ImagePlaceholderColor))
                },
                error = {
                    Box(Modifier.fillMaxSize().background(ImagePlaceholderColor))
                },
            )
        } else {
            Box(Modifier.fillMaxSize().background(ImagePlaceholderColor))
        }

        Box(modifier = Modifier.fillMaxSize().detailImageScrim())

        ActionButton(
            modifier = Modifier.padding(SpaceSize.large),
            onClick = onBackClick,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(Res.string.back_button_description),
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(SpaceSize.large),
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        ) {
            ActionButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(Res.string.restaurant_detail_favourite_description),
                    tint = if (isFavourite) FavouriteRed else Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }

            var isMenuExpanded by remember { mutableStateOf(false) }

            Box {
                ActionButton(onClick = { isMenuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(Res.string.restaurant_detail_more_options_description),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.restaurant_detail_share_button)) },
                        onClick = {
                            isMenuExpanded = false
                            onShareClick()
                        },
                    )
                    when (visitStatus) {
                        VisitStatus.WISH -> DropdownMenuItem(
                            text = { Text(stringResource(Res.string.restaurant_detail_move_to_visited_button)) },
                            onClick = {
                                isMenuExpanded = false
                                onMoveToVisitedClick()
                            },
                        )
                        VisitStatus.VISITED -> Unit
                        null -> DropdownMenuItem(
                            text = { Text(stringResource(Res.string.restaurant_detail_add_to_wishlist_button)) },
                            onClick = {
                                isMenuExpanded = false
                                onAddToWishlistClick()
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TitleSection(restaurant: Restaurant) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.small),
    ) {
        Text(
            text = restaurant.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(Modifier.height(SpaceSize.small))

        Text(
            text = restaurant.address,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun PhotoGallerySection(restaurant: Restaurant) {
    val galleryPhotos = restaurant.photoNames.drop(1).take(GALLERY_PHOTO_COUNT)
    if (galleryPhotos.isNotEmpty()) {
        PhotoGallery(
            photos = galleryPhotos,
            restaurantName = restaurant.name,
        )
        Spacer(Modifier.height(SpaceSize.large))
    }
}

@Composable
private fun PhotoGallery(photos: List<String>, restaurantName: String) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        contentPadding = PaddingValues(horizontal = SpaceSize.large),
    ) {
        items(photos) { photoUrl ->
            SubcomposeAsyncImage(
                model = "$photoUrl&size=300",
                contentDescription = restaurantName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(SpaceSize.medium)),
                loading = {
                    Box(
                        Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(SpaceSize.medium))
                            .background(ImagePlaceholderColor),
                    )
                },
                error = {
                    Box(
                        Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(SpaceSize.medium))
                            .background(ImagePlaceholderColor),
                    )
                },
            )
        }
    }
}

@Composable
private fun OpeningHoursSection(restaurant: Restaurant) {
    restaurant.regularOpeningHours?.let { openingHours ->
        Text(
            text = stringResource(Res.string.restaurant_detail_opening_hours_title),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = SpaceSize.large),
        )

        Spacer(Modifier.height(SpaceSize.large))

        OpeningHoursCard(openingHours)
    }
}

@Preview
@Composable
private fun RestaurantDetailScreenPreview() {
    val restaurant = Restaurant(
        id = "r1",
        name = "Le Jardin Français",
        description = "A charming French bistro",
        city = "Midtown",
        country = "French",
        countryCode = "French",
        postalCode = "French",
        photoNames = emptyList(),
        address = "123 Gourmet Street, Downtown, Lisbon",
        rating = 4.8,
        userRatingCount = 342,
        websiteUrl = "www.lejardin.com",
        phoneNumber = "+1 (555) 234-5678",
    )
    AppTheme {
        RestaurantDetailLoaded(
            restaurant = restaurant,
            isFavourite = false,
            visitStatus = null,
            onBackClick = {},
        )
    }
}
