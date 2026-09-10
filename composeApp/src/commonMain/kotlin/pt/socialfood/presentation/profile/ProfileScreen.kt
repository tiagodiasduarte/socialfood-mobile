package pt.socialfood.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.presentation.author.detail.AuthorDetailSkeleton
import pt.socialfood.presentation.author.detail.AuthorGuideCard
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.ProfileHeader
import pt.socialfood.presentation.components.TopActionButtons
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.author_detail_guides_section_title

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onGuideClick: (guideId: String) -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProfileContent(
        state = state,
        onBackClick = onBackClick,
        onGuideClick = onGuideClick,
        onRetry = viewModel::load,
    )
}

@Composable
private fun ProfileContent(
    state: ProfileUiState,
    onBackClick: () -> Unit,
    onGuideClick: (guideId: String) -> Unit = {},
    onRetry: () -> Unit,
) {
    when (state) {
        ProfileUiState.Loading -> AuthorDetailSkeleton()

        is ProfileUiState.Loaded -> ProfileLoaded(
            author = state.author,
            onBackClick = onBackClick,
            onGuideClick = onGuideClick,
        )

        is ProfileUiState.Error -> ProfileError(
            onBackClick = onBackClick,
            onRetry = onRetry,
        )
    }
}

@Composable
private fun ProfileLoaded(
    author: AuthorDetail,
    onBackClick: () -> Unit,
    onGuideClick: (guideId: String) -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        item {
            ProfileScreenHeader(
                author = author,
                onBackClick = onBackClick,
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
                    onClick = { onGuideClick(guide.id) },
                    modifier = Modifier.padding(horizontal = SpaceSize.large),
                )
                Spacer(Modifier.height(SpaceSize.large))
            }
        }
    }
}

@Composable
private fun ProfileScreenHeader(author: AuthorDetail, onBackClick: () -> Unit) {
    ProfileHeader(
        name = author.name,
        username = author.username,
        imageUrl = author.imageUrl,
        facebookUrl = author.facebookUrl,
        instagramUrl = author.instagramUrl,
        youtubeUrl = author.youtubeUrl,
        topAction = {
            TopActionButtons(
                showCloseButton = true,
                onCloseClick = onBackClick,
            )
        },
    )
}

@Composable
private fun ProfileError(onBackClick: () -> Unit, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
            TopActionButtons(
                showCloseButton = true,
                onCloseClick = onBackClick,
            )
        }

        ErrorContent(
            modifier = Modifier.fillMaxSize(),
            onRetryClick = onRetry,
        )
    }
}

@Preview
@Composable
private fun ProfileLoadingPreview() {
    AppTheme {
        ProfileContent(
            state = ProfileUiState.Loading,
            onBackClick = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun ProfileLoadedPreview() {
    val guides = listOf(
        AuthorDetail.Guide(
            id = "g1",
            name = "Michelin Star Favorites",
            description = "A curated collection of the finest dining experiences",
            imageUrl = "",
            numberOfRestaurant = 8,
        ),
        AuthorDetail.Guide(
            id = "g2",
            name = "Hidden Gems Lisbon",
            description = "Off the beaten path restaurants in Lisbon",
            imageUrl = "",
            numberOfRestaurant = 5,
        ),
    )
    val author = AuthorDetail(
        id = "a1",
        name = "Sarah Mitchell",
        username = "sarahmitchell",
        guidesCount = 12,
        followersCount = 2400,
        followingCount = 180,
        facebookUrl = "https://facebook.com/sarahmitchell",
        instagramUrl = "https://instagram.com/sarahmitchell",
        youtubeUrl = "https://youtube.com/@sarahmitchell",
        guides = guides,
    )
    AppTheme {
        ProfileContent(
            state = ProfileUiState.Loaded(author),
            onBackClick = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun ProfileErrorPreview() {
    AppTheme {
        ProfileError(onBackClick = {}, onRetry = {})
    }
}
