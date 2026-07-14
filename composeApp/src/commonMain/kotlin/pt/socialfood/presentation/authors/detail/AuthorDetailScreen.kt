package pt.socialfood.presentation.authors.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.author_detail_guides_section_title
import socialfood.composeapp.generated.resources.back_button_description
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.presentation.authors.follow.FollowButton
import pt.socialfood.presentation.components.ActionButton
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.UserImage
import pt.socialfood.presentation.profile.StatsRow
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize

internal val HeaderHeight = 180.dp
internal val AvatarSize = 96.dp
internal val AvatarRingSize = 108.dp
internal val AvatarOverlap = AvatarRingSize / 2

@Composable
fun AuthorDetailScreen(
    authorId: String,
    onBackClick: () -> Unit,
    viewModel: AuthorDetailViewModel = koinViewModel { parametersOf(authorId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AuthorDetailContent(
        state = state,
        onBackClick = onBackClick,
        onFollowClick = { },
        onRetry = viewModel::load,
    )
}

@Composable
private fun AuthorDetailContent(
    state: AuthorDetailUiState,
    onBackClick: () -> Unit,
    onFollowClick: (Author) -> Unit,
    onRetry: () -> Unit,
) {
    when (state) {
        AuthorDetailUiState.Loading -> AuthorDetailPlaceholder()

        is AuthorDetailUiState.Loaded -> AuthorDetailLoaded(
            author = state.author,
            onBackClick = onBackClick,
        )

        AuthorDetailUiState.Error -> AuthorDetailError(
            onBackClick = onBackClick,
            onRetry = onRetry,
        )
    }
}

@Composable
private fun AuthorDetailLoaded(
    author: AuthorDetail,
    onBackClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GreyBackground),
    ) {
        item {
            AuthorHeader(
                author = author,
                onBackClick = onBackClick
            )
        }

        if (author.guides.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(Res.string.author_detail_guides_section_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(
                        horizontal = SpaceSize.large,
                        vertical = SpaceSize.large,
                    ),
                )
            }

            itemsIndexed(author.guides, key = { _, g -> g.id }) { _, guide ->
                AuthorGuideCard(
                    guideName = guide.name,
                    guideDescription = guide.description,
                    numberOfRestaurant = guide.numberOfRestaurant,
                    imageUrl = guide.imageUrl,
                    modifier = Modifier.padding(horizontal = SpaceSize.large),
                )
                Spacer(Modifier.height(SpaceSize.large))
            }
        }
    }
}
@Composable
private fun AuthorHeader(
    author: AuthorDetail,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreyBackground),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HeaderHeight + AvatarOverlap),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HeaderHeight)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFF05A1A),
                                Color(0xFFB82010),
                            ),
                        ),
                    ),
            ) {
                ActionButton(
                    modifier = Modifier.padding(SpaceSize.large),
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(Res.string.back_button_description),
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
            Box(
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                Box(
                    modifier = Modifier
                        .size(AvatarRingSize)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    UserImage(
                        name = author.name,
                        imageUrl = author.imageUrl,
                        imageSize = AvatarSize
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSize.large),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = author.name,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(Modifier.height(SpaceSize.small))

            if (!author.bio.isNullOrBlank()) {
                Text(
                    text = author.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(SpaceSize.small))
            }

            StatsRow(
                guidesCount = author.guidesCount,
                followersCount = author.followersCount,
                followingCount = author.followingCount,
            )

            Spacer(Modifier.height(SpaceSize.small))

            FollowButton(
                authorId = author.id,
                isFollowing = author.isFollowing,
                onFollowClick = {}
            )
        }
    }
}

@Composable
private fun AuthorDetailError(
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
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
            onRetryClick = onRetry
        )
    }
}

@Preview
@Composable
private fun AuthorDetailLoadingPreview() {
    AppTheme {
        AuthorDetailContent(
            state = AuthorDetailUiState.Loading,
            onBackClick = {},
            onFollowClick = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun AuthorDetailLoadedPreview() {
    val guides = listOf(
        AuthorDetail.Guide(
            id = "g1",
            name = "Michelin Star Favorites",
            description = "A curated collection of the finest dining experiences",
            numberOfRestaurant = 8,
        ),
        AuthorDetail.Guide(
            id = "g2",
            name = "Hidden Gems Lisbon",
            description = "Off the beaten path restaurants in Lisbon",
            numberOfRestaurant = 5,
        ),
    )
    val author = AuthorDetail(
        id = "a1",
        name = "Sarah Mitchell",
        bio = "Food lover and culinary explorer. Discovering the best bites around the world.",
        guidesCount = 12,
        followersCount = 2400,
        followingCount = 180,
        isFollowing = false,
        guides = guides,
    )
    AppTheme {
        AuthorDetailContent(
            state = AuthorDetailUiState.Loaded(author),
            onBackClick = {},
            onFollowClick = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun AuthorDetailErrorPreview() {
    AppTheme {
        AuthorDetailError(onBackClick = {}, onRetry = {})
    }
}