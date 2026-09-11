package pt.socialfood.presentation.restaurant

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.IconSize
import pt.socialfood.ui.theme.PlaceholderStroke
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.restaurant_placeholder_icon

@Composable
fun RestaurantEmptyCard(modifier: Modifier = Modifier, text: String) {
    Card(
        modifier = modifier.fillMaxWidth().height(130.dp),
        shape = RoundedCornerShape(SpaceSize.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
            ) {
                Image(
                    painter = painterResource(Res.drawable.restaurant_placeholder_icon),
                    contentDescription = null,
                    modifier = Modifier.size(IconSize.small),
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium,
                    color = PlaceholderStroke,
                )
            }
        }
    }
}

@Composable
@Preview
private fun AuthorEmptyGuideCardPreview() {
    AppTheme {
        RestaurantEmptyCard(text = "No restaurants available")
    }
}
