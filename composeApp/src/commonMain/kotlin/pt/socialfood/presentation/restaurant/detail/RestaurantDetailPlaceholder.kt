package pt.socialfood.presentation.restaurant.detail

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.socialfood.presentation.components.ShimmerBox
import pt.socialfood.presentation.components.rememberShimmerAlpha
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize

@Composable
internal fun RestaurantDetailPlaceholder() {
    val alpha = rememberShimmerAlpha()

    Box(modifier = Modifier.fillMaxSize().background(GreyBackground)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 88.dp),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ImageHeight),
                ) {
                    ShimmerBox(modifier = Modifier.fillMaxSize(), alpha = alpha, shape = RoundedCornerShape(0.dp))

                    ShimmerBox(
                        modifier = Modifier
                            .padding(SpaceSize.large)
                            .size(40.dp),
                        alpha = alpha,
                        shape = CircleShape,
                    )

                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(SpaceSize.large),
                        horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
                    ) {
                        ShimmerBox(modifier = Modifier.size(40.dp), alpha = alpha, shape = CircleShape)
                        ShimmerBox(modifier = Modifier.size(40.dp), alpha = alpha, shape = CircleShape)
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = SpaceSize.large, vertical = SpaceSize.large),
                    verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
                ) {
                    ShimmerBox(modifier = Modifier.width(220.dp).height(26.dp), alpha = alpha)
                    ShimmerBox(modifier = Modifier.width(130.dp).height(16.dp), alpha = alpha)
                    ShimmerBox(modifier = Modifier.width(180.dp).height(14.dp), alpha = alpha)
                }
            }

            item {
                Spacer(Modifier.height(SpaceSize.large))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = SpaceSize.large),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
                ) {
                    Column(modifier = Modifier.padding(SpaceSize.large), verticalArrangement = Arrangement.spacedBy(SpaceSize.large)) {
                        InfoRowPlaceholder(alpha = alpha)
                        InfoRowPlaceholder(alpha = alpha, width = 120.dp)
                        InfoRowPlaceholder(alpha = alpha, width = 160.dp)
                    }
                }
            }

            item {
                Spacer(Modifier.height(SpaceSize.large))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = SpaceSize.large),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
                ) {
                    Row(
                        modifier = Modifier.padding(SpaceSize.large),
                        horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ShimmerBox(modifier = Modifier.size(22.dp), alpha = alpha, shape = CircleShape)
                        ShimmerBox(modifier = Modifier.width(32.dp).height(18.dp), alpha = alpha)
                        ShimmerBox(modifier = Modifier.width(80.dp).height(16.dp), alpha = alpha)
                    }
                }
            }
        }

        ShimmerBox(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(SpaceSize.large)
                .fillMaxWidth()
                .height(52.dp),
            alpha = alpha,
            shape = RoundedCornerShape(SpaceSize.large),
        )
    }
}

@Composable
private fun InfoRowPlaceholder(alpha: Float, width: androidx.compose.ui.unit.Dp = 200.dp) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
    ) {
        ShimmerBox(modifier = Modifier.size(20.dp), alpha = alpha, shape = CircleShape)
        ShimmerBox(modifier = Modifier.width(width).height(16.dp), alpha = alpha)
    }
}

@Preview
@Composable
private fun RestaurantDetailPlaceholderPreview() {
    AppTheme {
        RestaurantDetailPlaceholder()
    }
}
