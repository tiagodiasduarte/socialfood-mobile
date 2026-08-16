package pt.socialfood.presentation.restaurant.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pt.socialfood.presentation.components.ShimmerBox
import pt.socialfood.presentation.components.rememberShimmerAlpha
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize

private const val OPENING_HOURS_DAY_COUNT = 7
private const val GALLERY_THUMBNAIL_COUNT = 5

@Composable
internal fun RestaurantDetailPlaceholder() {
    val alpha = rememberShimmerAlpha()

    Box(modifier = Modifier.fillMaxSize().background(GreyBackground)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 88.dp),
        ) {
            item { HeroSectionPlaceholder(alpha) }
            item { TitleSectionPlaceholder(alpha) }
            item { PhotoGalleryPlaceholder(alpha) }
            item { SectionDividerPlaceholder() }
            item { InformationSectionPlaceholder(alpha) }
            item { SectionDividerPlaceholder() }
            item { OpeningHoursSectionPlaceholder(alpha) }
        }

        ShimmerBox(
            modifier =
            Modifier
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
private fun HeroSectionPlaceholder(alpha: Float) {
    Box(
        modifier =
        Modifier
            .fillMaxWidth()
            .height(ImageHeight),
    ) {
        ShimmerBox(modifier = Modifier.fillMaxSize(), alpha = alpha, shape = RoundedCornerShape(0.dp))

        ShimmerBox(
            modifier =
            Modifier
                .padding(SpaceSize.large)
                .size(40.dp),
            alpha = alpha,
            shape = CircleShape,
        )

        Row(
            modifier =
            Modifier
                .align(Alignment.TopEnd)
                .padding(SpaceSize.large),
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        ) {
            ShimmerBox(modifier = Modifier.size(40.dp), alpha = alpha, shape = CircleShape)
            ShimmerBox(modifier = Modifier.size(40.dp), alpha = alpha, shape = CircleShape)
        }
    }
}

@Composable
private fun TitleSectionPlaceholder(alpha: Float) {
    Column(
        modifier =
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.small),
    ) {
        ShimmerBox(modifier = Modifier.width(220.dp).height(22.dp), alpha = alpha)
        Spacer(Modifier.height(SpaceSize.small))
        ShimmerBox(modifier = Modifier.width(180.dp).height(16.dp), alpha = alpha)
    }
}

@Composable
private fun PhotoGalleryPlaceholder(alpha: Float) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        contentPadding = PaddingValues(horizontal = SpaceSize.large),
    ) {
        items(GALLERY_THUMBNAIL_COUNT) {
            ShimmerBox(
                modifier = Modifier.size(120.dp),
                alpha = alpha,
                shape = RoundedCornerShape(SpaceSize.medium),
            )
        }
    }

    Spacer(Modifier.height(SpaceSize.large))
}

@Composable
private fun SectionDividerPlaceholder() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = SpaceSize.large),
        color = MaterialTheme.colorScheme.outlineVariant,
    )
    Spacer(Modifier.height(SpaceSize.large))
}

@Composable
private fun InformationSectionPlaceholder(alpha: Float) {
    ShimmerBox(
        modifier = Modifier.padding(horizontal = SpaceSize.large).width(110.dp).height(18.dp),
        alpha = alpha,
    )

    Spacer(Modifier.height(SpaceSize.large))

    PlaceholderCard {
        Column(
            modifier = Modifier.padding(SpaceSize.large),
            verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            InfoRowPlaceholder(alpha = alpha)
            InfoRowPlaceholder(alpha = alpha, width = 120.dp)
            InfoRowPlaceholder(alpha = alpha, width = 160.dp)
        }
    }

    Spacer(Modifier.height(SpaceSize.xlarge))

    PlaceholderCard {
        Column(modifier = Modifier.padding(SpaceSize.large)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ShimmerBox(modifier = Modifier.size(24.dp), alpha = alpha, shape = CircleShape)
                Spacer(Modifier.width(SpaceSize.large))
                ShimmerBox(modifier = Modifier.width(140.dp).height(18.dp), alpha = alpha)
            }

            Spacer(Modifier.height(SpaceSize.medium))

            Row(verticalAlignment = Alignment.CenterVertically) {
                ShimmerBox(
                    modifier = Modifier.width(56.dp).height(28.dp),
                    alpha = alpha,
                    shape = RoundedCornerShape(4.dp),
                )
                Spacer(Modifier.width(SpaceSize.large))
                ShimmerBox(modifier = Modifier.width(80.dp).height(16.dp), alpha = alpha)
            }
        }
    }
}

@Composable
private fun OpeningHoursSectionPlaceholder(alpha: Float) {
    ShimmerBox(
        modifier = Modifier.padding(horizontal = SpaceSize.large).width(140.dp).height(18.dp),
        alpha = alpha,
    )

    Spacer(Modifier.height(SpaceSize.large))

    PlaceholderCard {
        Column(
            verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
            modifier = Modifier.padding(SpaceSize.large),
        ) {
            repeat(OPENING_HOURS_DAY_COUNT) { index ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ShimmerBox(modifier = Modifier.width(80.dp).height(16.dp), alpha = alpha)
                    ShimmerBox(modifier = Modifier.width(90.dp).height(16.dp), alpha = alpha)
                }
                if (index < OPENING_HOURS_DAY_COUNT - 1) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
private fun PlaceholderCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = SpaceSize.large),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
        content = content,
    )
}

@Composable
private fun InfoRowPlaceholder(alpha: Float, width: Dp = 200.dp) {
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
