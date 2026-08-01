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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.back_button_description
import socialfood.composeapp.generated.resources.restaurant_detail_call_button
import socialfood.composeapp.generated.resources.restaurant_detail_favourite_description
import socialfood.composeapp.generated.resources.restaurant_detail_opening_hours_title
import socialfood.composeapp.generated.resources.restaurant_detail_share_description
import socialfood.composeapp.generated.resources.share_icon
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.components.buttons.ActionButton
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.detailImageScrim
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.FavouriteRed
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize

val ImageHeight = 300.dp

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
    )
}

@Composable
private fun RestaurantDetailContent(
    state: RestaurantDetailUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onToggleFavourite: () -> Unit = {},
) {
    when (state) {
        RestaurantDetailUiState.Loading -> RestaurantDetailPlaceholder()

        is RestaurantDetailUiState.Loaded -> RestaurantDetailLoaded(
            restaurant = state.restaurant,
            isFavourite = state.isFavourite,
            onBackClick = onBackClick,
            onToggleFavourite = onToggleFavourite,
        )

        RestaurantDetailUiState.Error -> ErrorContent(onRetryClick = onRetry)
    }
}

@Composable
private fun RestaurantDetailLoaded(
    restaurant: Restaurant,
    isFavourite: Boolean,
    onBackClick: () -> Unit,
    onToggleFavourite: () -> Unit = {},
) {
    val uriHandler = LocalUriHandler.current

    Box(modifier = Modifier.fillMaxSize().background(GreyBackground)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 88.dp),
        ) {
            item {
                TopSection(
                    restaurant = restaurant,
                    isFavourite = isFavourite,
                    onBackClick = onBackClick,
                    onShareClick = {},
                    onFavoriteClick = onToggleFavourite,
                )
            }

            item {
                TitleSection(restaurant = restaurant)
            }

            if (restaurant.photoNames.isNotEmpty()) {
                item {
                    PhotoGallery(
                        photos = restaurant.photoNames,
                        restaurantName = restaurant.name,
                    )
                    Spacer(Modifier.height(SpaceSize.large))
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = SpaceSize.large),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
                Spacer(Modifier.height(SpaceSize.large))
            }

            item {
                InformationSection(
                    restaurant = restaurant,
                    onNavigateClick = {
                        if (restaurant.address.isNotBlank())
                            uriHandler.openUri("geo:0,0?q=${restaurant.address}")
                    },
                    onWebsiteClick = {
                        if (!restaurant.websiteUrl.isNullOrBlank())
                            uriHandler.openUri(restaurant.websiteUrl)
                    },
                )
            }

            item {
                Spacer(Modifier.height(SpaceSize.xlarge))

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = SpaceSize.large),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )

                Spacer(Modifier.height(SpaceSize.xlarge))
            }

            restaurant.regularOpeningHours?.let {
                item {
                    Text(
                        text = stringResource(Res.string.restaurant_detail_opening_hours_title),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = SpaceSize.large),
                    )

                    Spacer(Modifier.height(SpaceSize.large))

                    OpeningHoursCard(it)
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, GreyBackground),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY,
                    ),
                )
                .padding(SpaceSize.large),
        ) {
            Button(
                onClick = {
                    if (restaurant.phoneNumber.isNotBlank())
                        uriHandler.openUri("tel:${restaurant.phoneNumber}")
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                shape = RoundedCornerShape(SpaceSize.large),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(SpaceSize.medium))
                Text(
                    text = stringResource(Res.string.restaurant_detail_call_button),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun TopSection(
    restaurant: Restaurant,
    isFavourite: Boolean,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onFavoriteClick: () -> Unit,
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
                    Box(Modifier.fillMaxSize().background(Color(0xFF2A2A2A)))
                },
                error = {
                    Box(Modifier.fillMaxSize().background(Color(0xFF2A2A2A)))
                },
            )
        } else {
            Box(Modifier.fillMaxSize().background(Color(0xFF2A2A2A)))
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
            ActionButton(onClick = onShareClick) {
                Icon(
                    painter = painterResource(Res.drawable.share_icon),
                    contentDescription = stringResource(Res.string.restaurant_detail_share_description),
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
            ActionButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(Res.string.restaurant_detail_favourite_description),
                    tint = if (isFavourite) FavouriteRed else Color.White,
                    modifier = Modifier.size(24.dp),
                )
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
                            .background(Color(0xFF2A2A2A)),
                    )
                },
                error = {
                    Box(
                        Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(SpaceSize.medium))
                            .background(Color(0xFF2A2A2A)),
                    )
                },
            )
        }
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
            onBackClick = {},
        )
    }
}
