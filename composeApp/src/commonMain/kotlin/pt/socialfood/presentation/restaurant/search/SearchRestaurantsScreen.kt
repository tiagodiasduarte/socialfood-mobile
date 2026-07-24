package pt.socialfood.presentation.restaurant.search

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.search_restaurants_error
import socialfood.composeapp.generated.resources.search_restaurants_search_placeholder
import socialfood.composeapp.generated.resources.search_restaurants_title
import pt.socialfood.domain.model.Place
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.components.SearchBar
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun SearchRestaurantsScreen(
    onBackClick: () -> Unit,
    onRestaurantAdded: (Restaurant) -> Unit,
    viewModel: SearchRestaurantsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isImportingRestaurant by viewModel.isImportingRestaurant.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SearchRestaurantsViewModel.UiEvent.RestaurantAdded -> onRestaurantAdded(event.restaurant)
            }
        }
    }

    SearchRestaurantsContent(
        state = state,
        searchQuery = viewModel.searchQuery,
        isImportingRestaurant = isImportingRestaurant,
        onBackClick = onBackClick,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onRestaurantClicked = {
            viewModel.onAddRestaurant(placeId = it)
        },
    )
}

@Composable
private fun SearchRestaurantsContent(
    state: SearchRestaurantsUiState,
    searchQuery: String,
    isImportingRestaurant: Boolean,
    onBackClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onRestaurantClicked: (String) -> Unit,
) {
    if (isImportingRestaurant) {
        ImportRestaurantDialog()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        TopBar(onBackClick = onBackClick)

        Spacer(Modifier.height(SpaceSize.large))

        SearchBar(
            searchQuery = searchQuery,
            onQueryChange = onSearchQueryChange,
            placeholder = stringResource(Res.string.search_restaurants_search_placeholder),
        )

        Spacer(Modifier.height(SpaceSize.large))

        when (val current = state) {
            SearchRestaurantsUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }

            is SearchRestaurantsUiState.Loaded -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = SpaceSize.large,
                    vertical = SpaceSize.medium
                ),
                verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
            ) {
                items(current.places, key = { it.id }) { place ->
                    PlaceItem(
                        place = place,
                        onAddClicked = { onRestaurantClicked(place.id) },
                    )
                }
            }

            SearchRestaurantsUiState.Error -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.search_restaurants_error),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSize.medium),
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
            text = stringResource(Res.string.search_restaurants_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}


@Composable
@Preview
fun SearchRestaurantsScreenPreview() {
    val places = listOf(
        Place(id = "1", name = "Le Jardin", address = "Lisbon", imageUrl = null),
        Place(id = "2", name = "Terra", address = "Coimbra", imageUrl = null),
        Place(id = "3", name = "Amber", address = "Lisbon", imageUrl = null),
    )
    AppTheme {
        SearchRestaurantsContent(
            state = SearchRestaurantsUiState.Loaded(places),
            searchQuery = "",
            isImportingRestaurant = false,
            onBackClick = {},
            onSearchQueryChange = {},
            onRestaurantClicked = {},
        )
    }
}
