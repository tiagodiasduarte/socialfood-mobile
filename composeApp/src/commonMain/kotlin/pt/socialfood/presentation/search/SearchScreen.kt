package pt.socialfood.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.Search
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.NoResultsContent
import pt.socialfood.presentation.components.SearchBar
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.search_search_placeholder
import socialfood.composeapp.generated.resources.search_section_authors_title
import socialfood.composeapp.generated.resources.search_section_guides_title
import socialfood.composeapp.generated.resources.search_section_restaurants_title

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    onAuthorClick: (authorId: String) -> Unit = {},
    onGuideClick: (guideId: String) -> Unit = {},
    onRestaurantClick: (restaurantId: String) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchQuery by viewModel.query.collectAsStateWithLifecycle()
    val suggestionResultsRequested by viewModel.suggestionResultsRequested.collectAsStateWithLifecycle()
    val activeSuggestionSource by viewModel.activeSuggestionSource.collectAsStateWithLifecycle()

    SearchScreenContent(
        searchQuery = searchQuery,
        state = state,
        suggestionResultsRequested = suggestionResultsRequested,
        activeSuggestionSource = activeSuggestionSource,
        onQueryChange = viewModel::onSearchQueryChange,
        onFavoriteGuidesClick = viewModel::onFavoriteGuidesClick,
        onFavoriteRestaurantsClick = viewModel::onFavoriteRestaurantsClick,
        onRetrySuggestions = viewModel::retrySuggestions,
        onClearSuggestions = viewModel::onClearSuggestions,
        onResultClick = { result ->
            when (result) {
                is Search.AuthorResult -> onAuthorClick(result.author.id)
                is Search.GuideResult -> onGuideClick(result.guide.id)
                is Search.RestaurantResult -> onRestaurantClick(result.restaurant.id)
            }
        },
    )
}

@Suppress("LongMethod")
@Composable
fun SearchScreenContent(
    searchQuery: String,
    state: SearchUiState,
    suggestionResultsRequested: Boolean = false,
    activeSuggestionSource: SuggestionSource? = null,
    onQueryChange: (String) -> Unit = {},
    onFavoriteGuidesClick: () -> Unit = {},
    onFavoriteRestaurantsClick: () -> Unit = {},
    onRetrySuggestions: () -> Unit = {},
    onClearSuggestions: () -> Unit = {},
    onResultClick: (Search) -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize().background(GreyBackground)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = SpaceSize.large),
        ) {
            if (activeSuggestionSource != null) {
                SearchBar(
                    placeholder = stringResource(Res.string.search_search_placeholder),
                    searchQuery = stringResource(activeSuggestionSource.labelRes()),
                    onQueryChange = { onClearSuggestions() },
                    enabled = false,
                )
            } else {
                SearchBar(
                    placeholder = stringResource(Res.string.search_search_placeholder),
                    searchQuery = searchQuery,
                    onQueryChange = onQueryChange,
                )
            }
        }

        if (searchQuery.length < MIN_QUERY_LENGTH && !suggestionResultsRequested) {
            SearchSuggestionsContent(
                modifier = Modifier.fillMaxSize(),
                onFavoriteGuidesClick = onFavoriteGuidesClick,
                onFavoriteRestaurantsClick = onFavoriteRestaurantsClick,
            )
        } else {
            when (state) {
                is SearchUiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }

                is SearchUiState.Error -> ErrorContent(
                    modifier = Modifier.fillMaxSize(),
                    onRetryClick = {
                        if (suggestionResultsRequested) onRetrySuggestions() else onQueryChange(searchQuery)
                    },
                )

                is SearchUiState.Loaded -> if (state.results.isEmpty()) {
                    NoResultsContent(modifier = Modifier.fillMaxSize())
                } else {
                    SearchResultsList(results = state.results, onResultClick = onResultClick)
                }
            }
        }
    }
}

@Composable
private fun SearchResultsList(results: List<Search>, onResultClick: (Search) -> Unit) {
    val restaurants = results.filterIsInstance<Search.RestaurantResult>()
    val guides = results.filterIsInstance<Search.GuideResult>()
    val authors = results.filterIsInstance<Search.AuthorResult>()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        contentPadding = PaddingValues(vertical = SpaceSize.medium),
    ) {
        if (restaurants.isNotEmpty()) {
            item {
                SearchSectionHeader(
                    icon = Icons.Outlined.LocationOn,
                    title = stringResource(Res.string.search_section_restaurants_title),
                )
            }
            items(restaurants, key = { it.id }) { result ->
                SearchRestaurantItem(
                    restaurant = result.restaurant,
                    modifier = Modifier.padding(horizontal = SpaceSize.large),
                    onClick = { onResultClick(result) },
                )
            }
        }

        if (guides.isNotEmpty()) {
            item {
                SearchSectionHeader(
                    icon = Icons.AutoMirrored.Outlined.MenuBook,
                    title = stringResource(Res.string.search_section_guides_title),
                )
            }
            items(guides, key = { it.id }) { result ->
                SearchGuideItem(
                    guide = result.guide,
                    modifier = Modifier.padding(horizontal = SpaceSize.large),
                    onClick = { onResultClick(result) },
                )
            }
        }

        if (authors.isNotEmpty()) {
            item {
                SearchSectionHeader(
                    icon = Icons.Outlined.Person,
                    title = stringResource(Res.string.search_section_authors_title),
                )
            }
            items(authors, key = { it.id }) { result ->
                SearchAuthorItem(
                    author = result.author,
                    modifier = Modifier.padding(horizontal = SpaceSize.large),
                    onClick = { onResultClick(result) },
                )
            }
        }
    }
}

@Composable
@Preview
private fun SearchScreenPreview() {
    AppTheme {
        SearchScreenContent(
            searchQuery = "belcanto",
            state = SearchUiState.Loaded(
                listOf(
                    Search.RestaurantResult(
                        Restaurant(
                            id = "1",
                            name = "Terra",
                            description = "Italian",
                            city = "Porto",
                            country = "Portugal",
                            countryCode = "PT",
                            postalCode = null,
                            photoNames = emptyList(),
                            address = "Rua de Cedofeita",
                            rating = 4.7,
                            userRatingCount = 500,
                            websiteUrl = null,
                            phoneNumber = "+351000000000",
                        ),
                    ),
                    Search.GuideResult(
                        Guide(
                            id = "2",
                            name = "Michelin Star Favorites",
                            description = "The finest dining experiences",
                            visibility = GuideVisibility.PUBLIC,
                            author = Author(id = "a1", name = "Sarah M.", username = "sarahm"),
                            numberOfRestaurant = 8,
                        ),
                    ),
                    Search.AuthorResult(
                        Author(id = "3", name = "Sarah M.", username = "sarahm"),
                    ),
                ),
            ),
        )
    }
}
