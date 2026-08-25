package pt.socialfood.presentation.guide.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pt.socialfood.domain.model.Restaurant

@Composable
expect fun RestaurantMapView(restaurants: List<Restaurant>, modifier: Modifier)
