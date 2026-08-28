@file:Suppress("TooManyFunctions")

package pt.socialfood.presentation.guide.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.TopActionButtons
import pt.socialfood.presentation.components.buttons.OutlinedButton
import pt.socialfood.presentation.components.detailImageScrim
import pt.socialfood.presentation.components.placeholder.GuideCardPlaceholder
import pt.socialfood.presentation.guide.detail.author.AuthorItemCard
import pt.socialfood.presentation.restaurant.RestaurantSmallCard
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.ImagePlaceholderColor
import pt.socialfood.ui.theme.PrivateBadge
import pt.socialfood.ui.theme.PublicBadge
import pt.socialfood.ui.theme.PublicBadgeBackground
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.guide_detail_map_button_description
import socialfood.composeapp.generated.resources.guide_detail_private_icon_description
import socialfood.composeapp.generated.resources.guide_detail_private_label
import socialfood.composeapp.generated.resources.guide_detail_public_icon_description
import socialfood.composeapp.generated.resources.guide_detail_public_label
import socialfood.composeapp.generated.resources.guide_detail_restaurants_count_label
import socialfood.composeapp.generated.resources.guide_detail_restaurants_section_title
import socialfood.composeapp.generated.resources.guide_detail_separator
import socialfood.composeapp.generated.resources.guides_private_icon
import socialfood.composeapp.generated.resources.guides_public_icon

internal val GuideImageHeight = 320.dp

@Composable
fun GuideDetailScreen(
    guideId: String,
    onBackClick: () -> Unit,
    onEditClick: (guideId: String) -> Unit = {},
    onRestaurantClick: (restaurantId: String) -> Unit = {},
    onAuthorClick: (authorId: String) -> Unit = {},
    onViewMapClick: (guideId: String, guideName: String, restaurantsCount: Int) -> Unit = { _, _, _ -> },
    viewModel: GuideDetailViewModel = koinViewModel { parametersOf(guideId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    GuideDetailContent(
        state = state,
        onEditClick = onEditClick,
        onBackClick = onBackClick,
        onRestaurantClick = onRestaurantClick,
        onAuthorClick = onAuthorClick,
        onViewMapClick = onViewMapClick,
        onRetry = viewModel::load,
        onToggleFavourite = viewModel::toggleFavourite,
    )
}

@Composable
private fun GuideDetailContent(
    state: GuideDetailUiState,
    onEditClick: (id: String) -> Unit,
    onBackClick: () -> Unit,
    onRestaurantClick: (restaurantId: String) -> Unit = {},
    onAuthorClick: (authorId: String) -> Unit = {},
    onViewMapClick: (guideId: String, guideName: String, restaurantsCount: Int) -> Unit = { _, _, _ -> },
    onRetry: () -> Unit = {},
    onToggleFavourite: () -> Unit = {},
) {
    when (state) {
        GuideDetailUiState.Loading -> GuideDetailSkeleton()

        is GuideDetailUiState.Loaded ->
            GuideDetailLoaded(
                guide = state.guide,
                currentUserId = state.currentUserId,
                isFavourite = state.isFavourite,
                onEditClick = { onEditClick(it) },
                onBackClick = onBackClick,
                onRestaurantClick = onRestaurantClick,
                onAuthorClick = onAuthorClick,
                onViewMapClick = onViewMapClick,
                onToggleFavourite = onToggleFavourite,
            )

        is GuideDetailUiState.Error ->
            GuideDetailError(
                onBackClick = onBackClick,
                onRetry = onRetry,
            )
    }
}

@Composable
private fun GuideDetailError(onBackClick: () -> Unit, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
            TopActionButtons(
                showCloseButton = true,
                onCloseClick = onBackClick,
            )
        }

        ErrorContent(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = MaterialTheme.colorScheme.surface,
            onRetryClick = onRetry,
        )
    }
}

@Composable
private fun GuideDetailLoaded(
    guide: Guide,
    currentUserId: String?,
    isFavourite: Boolean,
    onEditClick: (id: String) -> Unit,
    onBackClick: () -> Unit,
    onRestaurantClick: (restaurantId: String) -> Unit = {},
    onAuthorClick: (authorId: String) -> Unit = {},
    onViewMapClick: (guideId: String, guideName: String, restaurantsCount: Int) -> Unit = { _, _, _ -> },
    onToggleFavourite: () -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        item {
            TopImageContent(
                guide = guide,
                currentUserId = currentUserId,
                isFavourite = isFavourite,
                onEditClick = { onEditClick(it) },
                onBackClick = onBackClick,
                onToggleFavourite = onToggleFavourite,
            )

            GuideInfo(guide)

            GuideTitleAndDescription(guide)

            Spacer(Modifier.height(SpaceSize.large))

            AuthorItemCard(
                modifier = Modifier.padding(horizontal = SpaceSize.large),
                author = guide.author,
                onClick = { onAuthorClick(guide.author.id) },
            )
        }

        if (guide.restaurants.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSize.large),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(Res.string.guide_detail_restaurants_section_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    OutlinedButton(
                        text = stringResource(Res.string.guide_detail_map_button_description),
                        icon = Icons.Outlined.Map,
                        onClick = { onViewMapClick(guide.id, guide.name, guide.restaurants.size) },
                    )
                }
            }

            itemsIndexed(guide.restaurants, key = { _, r -> r.id }) { _, restaurant ->
                RestaurantSmallCard(
                    modifier = Modifier.padding(horizontal = SpaceSize.large),
                    restaurant = restaurant,
                    onClick = { onRestaurantClick(restaurant.id) },
                )
            }
        }

        item { Spacer(Modifier.height(SpaceSize.xxlarge)) }
    }
}

@Composable
private fun TopImageContent(
    guide: Guide,
    currentUserId: String?,
    isFavourite: Boolean,
    onEditClick: (id: String) -> Unit,
    onBackClick: () -> Unit,
    onToggleFavourite: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(GuideImageHeight),
    ) {
        if (guide.imageUrl != null) {
            SubcomposeAsyncImage(
                model = guide.imageUrl,
                contentDescription = guide.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = { GuideCardPlaceholder() },
                error = { Box(Modifier.fillMaxSize().background(ImagePlaceholderColor)) },
            )
        } else {
            GuideCardPlaceholder()
        }

        Box(modifier = Modifier.fillMaxSize().detailImageScrim())

        val isOwnGuide = guide.author.id == currentUserId

        TopActionButtons(
            showCloseButton = true,
            onCloseClick = onBackClick,
            showShareButton = !isOwnGuide,
            showEditButton = isOwnGuide,
            onEditClick = { onEditClick(guide.id) },
            showFavouriteButton = true,
            isFavourite = isFavourite,
            onToggleFavourite = onToggleFavourite,
        )
    }
}

@Composable
private fun GuideInfo(guide: Guide) {
    Row(
        modifier = Modifier.padding(SpaceSize.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        Row(
            modifier = Modifier
                .height(24.dp)
                .clip(RoundedCornerShape(SpaceSize.medium))
                .background(guide.visibility.badgeBackgroundColor())
                .padding(horizontal = SpaceSize.medium, vertical = SpaceSize.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.small),
        ) {
            when (guide.visibility) {
                GuideVisibility.PUBLIC -> {
                    Image(
                        painter = painterResource(Res.drawable.guides_public_icon),
                        contentDescription = stringResource(Res.string.guide_detail_public_icon_description),
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(PublicBadge),
                    )
                    Text(
                        text = stringResource(Res.string.guide_detail_public_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = PublicBadge,
                    )
                }

                GuideVisibility.PRIVATE -> {
                    Image(
                        painter = painterResource(Res.drawable.guides_private_icon),
                        contentDescription = stringResource(Res.string.guide_detail_private_icon_description),
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(PrivateBadge),
                    )
                    Text(
                        text = stringResource(Res.string.guide_detail_private_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = PrivateBadge,
                    )
                }
            }
        }

        Text(
            text = stringResource(Res.string.guide_detail_separator),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = stringResource(
                Res.string.guide_detail_restaurants_count_label,
                guide.numberOfRestaurant,
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun GuideTitleAndDescription(guide: Guide) {
    Text(
        modifier = Modifier.padding(horizontal = SpaceSize.large),
        text = guide.name,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
    )

    Spacer(Modifier.height(SpaceSize.large))

    if (guide.description.isNotBlank()) {
        Text(
            modifier = Modifier.padding(horizontal = SpaceSize.large),
            text = guide.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun GuideVisibility.badgeBackgroundColor(): Color =
    if (this == GuideVisibility.PUBLIC) PublicBadgeBackground else MaterialTheme.colorScheme.surface

@Composable
@Preview
fun GuideDetailScreenPreview() {
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
        description = "A carefully curated collection of the finest dining experiences in the city. " +
            "Each restaurant has been personally visited and reviewed to ensure exceptional quality, " +
            "impeccable service, and unforgettable culinary moments.",
        numberOfRestaurant = 8,
        visibility = GuideVisibility.PUBLIC,
        author = author,
        restaurants = listOf(restaurant),
    )
    AppTheme {
        GuideDetailContent(
            state = GuideDetailUiState.Loaded(guide, currentUserId = null),
            onEditClick = {},
            onBackClick = {},
        )
    }
}

@Composable
@Preview
fun GuideDetailScreenLoadingPreview() {
    AppTheme {
        GuideDetailContent(
            state = GuideDetailUiState.Loading,
            onEditClick = {},
            onBackClick = {},
        )
    }
}

@Composable
@Preview
fun GuideDetailScreenErrorPreview() {
    AppTheme {
        GuideDetailContent(
            state = GuideDetailUiState.Error(ErrorCode.GUIDE_NOT_FOUND),
            onEditClick = {},
            onBackClick = {},
        )
    }
}
