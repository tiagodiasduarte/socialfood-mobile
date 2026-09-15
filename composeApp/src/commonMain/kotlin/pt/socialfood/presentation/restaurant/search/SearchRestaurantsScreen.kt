package pt.socialfood.presentation.restaurant.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pt.socialfood.domain.model.Place
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.NoResultsContent
import pt.socialfood.presentation.components.SearchBar
import pt.socialfood.presentation.components.TopActionBar
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.IdleContentBackground
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.restaurant_icon
import socialfood.composeapp.generated.resources.search_restaurants_idle_subtitle
import socialfood.composeapp.generated.resources.search_restaurants_idle_subtitle_bold
import socialfood.composeapp.generated.resources.search_restaurants_idle_title
import socialfood.composeapp.generated.resources.search_restaurants_no_results_subtitle
import socialfood.composeapp.generated.resources.search_restaurants_no_results_title
import socialfood.composeapp.generated.resources.search_restaurants_search_placeholder
import socialfood.composeapp.generated.resources.search_restaurants_title

@Composable
fun SearchRestaurantsScreen(
    guideId: String,
    onBackClick: () -> Unit,
    onRestaurantAdded: (Restaurant) -> Unit,
    viewModel: SearchRestaurantsViewModel = koinViewModel(parameters = { parametersOf(guideId) }),
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
        Header(
            searchQuery = searchQuery,
            onBackClick = onBackClick,
            onSearchQueryChange = onSearchQueryChange,
        )

        when (state) {
            SearchRestaurantsUiState.Loading -> LoadingContent()

            is SearchRestaurantsUiState.Loaded -> LoadedContent(
                state = state,
                searchQuery = searchQuery,
                onRestaurantClicked = onRestaurantClicked,
            )

            is SearchRestaurantsUiState.Error -> ErrorContent(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun LoadedContent(
    state: SearchRestaurantsUiState.Loaded,
    searchQuery: String,
    onRestaurantClicked: (String) -> Unit,
) {
    when {
        state.places.isEmpty() && searchQuery.isBlank() -> IdleContent(
            title = stringResource(Res.string.search_restaurants_idle_title),
            subtitle = idleSubtitle(),
            modifier = Modifier.fillMaxSize(),
        )

        state.places.isEmpty() -> NoResultsContent(
            title = stringResource(Res.string.search_restaurants_no_results_title),
            subtitle = stringResource(Res.string.search_restaurants_no_results_subtitle),
            modifier = Modifier.fillMaxSize(),
        )

        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = SpaceSize.large,
                vertical = SpaceSize.medium,
            ),
            verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        ) {
            items(state.places, key = { it.id }) { place ->
                PlaceItem(
                    place = place,
                    onAddClicked = { onRestaurantClicked(place.id) },
                )
            }
        }
    }
}

@Composable
private fun Header(searchQuery: String, onBackClick: () -> Unit, onSearchQueryChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        TopActionBar(
            title = stringResource(Res.string.search_restaurants_title),
            onBackClick = onBackClick,
        )

        Spacer(Modifier.height(SpaceSize.medium))

        SearchBar(
            searchQuery = searchQuery,
            onQueryChange = onSearchQueryChange,
            placeholder = stringResource(Res.string.search_restaurants_search_placeholder),
        )

        Spacer(Modifier.height(SpaceSize.medium))
    }
}

@Composable
private fun idleSubtitle(): AnnotatedString {
    val template = stringResource(Res.string.search_restaurants_idle_subtitle)
    val boldWord = stringResource(Res.string.search_restaurants_idle_subtitle_bold)
    val (prefix, suffix) = template.split("%1\$s", limit = 2).let { it[0] to it.getOrElse(1) { "" } }

    return buildAnnotatedString {
        append(prefix)
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(boldWord) }
        append(suffix)
    }
}

@Composable
fun IdleContent(title: String, subtitle: AnnotatedString, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = SpaceSize.xlarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(IdleContentBackground),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.restaurant_icon),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(Modifier.height(SpaceSize.large))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(SpaceSize.medium))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
@Preview
fun SearchRestaurantsScreenIdlePreview() {
    AppTheme {
        SearchRestaurantsContent(
            state = SearchRestaurantsUiState.Loaded(emptyList()),
            searchQuery = "",
            isImportingRestaurant = false,
            onBackClick = {},
            onSearchQueryChange = {},
            onRestaurantClicked = {},
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
