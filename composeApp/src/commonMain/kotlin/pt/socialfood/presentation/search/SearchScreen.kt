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
import pt.socialfood.domain.model.Search
import pt.socialfood.domain.model.SearchResultType
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.NoResultsContent
import pt.socialfood.presentation.components.SearchBar
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.search_search_placeholder

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    onAuthorClick: (authorId: String) -> Unit = {},
    onGuideClick: (guideId: String) -> Unit = {},
    onRestaurantClick: (restaurantId: String) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SearchScreenContent(
        searchQuery = viewModel.searchQuery,
        state = state,
        onQueryChange = viewModel::onSearchQueryChange,
        onResultClick = { result ->
            when (result.type) {
                SearchResultType.AUTHOR -> onAuthorClick(result.id)
                SearchResultType.GUIDE -> onGuideClick(result.id)
                SearchResultType.RESTAURANT -> onRestaurantClick(result.id)
            }
        },
    )
}

@Composable
fun SearchScreenContent(
    searchQuery: String,
    state: SearchUiState,
    onQueryChange: (String) -> Unit = {},
    onResultClick: (Search) -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize().background(GreyBackground)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = SpaceSize.large),
        ) {
            SearchBar(
                placeholder = stringResource(Res.string.search_search_placeholder),
                searchQuery = searchQuery,
                onQueryChange = onQueryChange,
            )
        }

        when (state) {
            is SearchUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is SearchUiState.Error -> ErrorContent(
                modifier = Modifier.fillMaxSize(),
                onRetryClick = { onQueryChange(searchQuery) },
            )

            is SearchUiState.Loaded -> if (state.results.isEmpty()) {
                if (searchQuery.isNotBlank()) {
                    NoResultsContent(modifier = Modifier.fillMaxSize())
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().background(Color.White),
                    verticalArrangement = Arrangement.spacedBy(SpaceSize.small),
                    contentPadding = PaddingValues(vertical = SpaceSize.medium),
                ) {
                    items(state.results, key = { "${it.type}_${it.id}" }) { result ->
                        SearchResultItem(
                            result = result,
                            onClick = { onResultClick(result) },
                        )
                    }
                }
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
                    Search(
                        id = "1",
                        name = "Belcanto",
                        description = "Fine dining in Lisbon",
                        type = SearchResultType.RESTAURANT,
                    ),
                    Search(
                        id = "2",
                        name = "Michelin Star Favorites",
                        description = "The finest dining experiences",
                        type = SearchResultType.GUIDE,
                    ),
                    Search(
                        id = "3",
                        name = "Sarah M.",
                        description = "@sarahm",
                        type = SearchResultType.AUTHOR,
                    ),
                ),
            ),
        )
    }
}
