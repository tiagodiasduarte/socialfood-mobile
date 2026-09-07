package pt.socialfood.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pt.socialfood.domain.model.Restaurant

@Composable
expect fun MapRestaurantView(restaurants: List<Restaurant>, modifier: Modifier)
