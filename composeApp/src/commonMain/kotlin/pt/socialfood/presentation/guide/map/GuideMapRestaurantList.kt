package pt.socialfood.presentation.guide.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.map.MapRestaurantView
import pt.socialfood.presentation.restaurant.RestaurantMapCard
import pt.socialfood.ui.theme.SpaceSize

private val MapCardWidth = 300.dp

@Composable
internal fun GuideMapWithList(restaurants: List<Restaurant>, modifier: Modifier = Modifier) {
    var selectedRestaurantId by remember { mutableStateOf<String?>(null) }
    var isListExpanded by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()

    LaunchedEffect(selectedRestaurantId) {
        val index = restaurants.indexOfFirst { it.id == selectedRestaurantId }
        if (index >= 0) {
            listState.animateScrollToItem(index)
            listState.centerItem(index)
        }
    }

    Box(modifier = modifier) {
        MapRestaurantView(
            restaurants = restaurants,
            selectedRestaurantId = selectedRestaurantId,
            onRestaurantSelected = { id ->
                selectedRestaurantId = id
                isListExpanded = true
            },
            onMapClick = { isListExpanded = false },
            modifier = Modifier.fillMaxSize(),
        )

        AnimatedVisibility(
            visible = isListExpanded,
            modifier = Modifier.align(Alignment.BottomStart),
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
        ) {
            RestaurantMapList(
                restaurants = restaurants,
                listState = listState,
                onRestaurantClick = { id -> selectedRestaurantId = id },
            )
        }
    }
}

private suspend fun LazyListState.centerItem(index: Int) {
    val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index } ?: return
    val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
    val itemCenter = itemInfo.offset + itemInfo.size / 2
    animateScrollBy((itemCenter - viewportCenter).toFloat())
}

@Composable
private fun RestaurantMapList(
    restaurants: List<Restaurant>,
    listState: LazyListState,
    onRestaurantClick: (String) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(vertical = SpaceSize.medium),
    ) {
        val horizontalPadding = ((maxWidth - MapCardWidth).coerceAtLeast(0.dp)) / 2

        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
        ) {
            items(restaurants, key = { it.id }) { restaurant ->
                RestaurantMapCard(
                    restaurant = restaurant,
                    onClick = { onRestaurantClick(restaurant.id) },
                )
            }
        }
    }
}
