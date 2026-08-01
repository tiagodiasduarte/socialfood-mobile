package pt.socialfood.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.socialfood.presentation.components.ProfileHeaderPlaceholder
import pt.socialfood.presentation.components.ProfileHeaderTopActionPlaceholder
import pt.socialfood.presentation.components.ShimmerBox
import pt.socialfood.presentation.components.rememberShimmerAlpha
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun ProfilePlaceholder(modifier: Modifier = Modifier) {
    val alpha = rememberShimmerAlpha()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GreyBackground)
            .verticalScroll(rememberScrollState()),
    ) {
        ProfileHeaderPlaceholder(alpha = alpha) {
            ProfileHeaderTopActionPlaceholder(
                alpha = alpha,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpaceSize.large)
                .padding(vertical = SpaceSize.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            ContactCardPlaceholder(alpha = alpha)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
            ) {
                repeat(3) {
                    MenuRowPlaceholder(alpha = alpha)
                }
            }
        }
    }
}

@Composable
private fun ContactCardPlaceholder(alpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(SpaceSize.large))
            .background(Color.White)
            .padding(SpaceSize.large),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ShimmerBox(modifier = Modifier.width(80.dp).height(14.dp), alpha = alpha)

            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(vertical = SpaceSize.large),
                alpha = alpha,
            )

            Spacer(Modifier.height(SpaceSize.large))

            Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.large)) {
                repeat( 1) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
                    ) {
                        ShimmerBox(modifier = Modifier.size(20.dp), alpha = alpha)
                        ShimmerBox(modifier = Modifier.width(180.dp).height(14.dp), alpha = alpha)
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuRowPlaceholder(alpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(SpaceSize.large))
            .background(Color.White)
            .padding(SpaceSize.large),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
            ) {
                ShimmerBox(modifier = Modifier.size(22.dp), alpha = alpha)
                ShimmerBox(modifier = Modifier.width(120.dp).height(14.dp), alpha = alpha)
            }
            ShimmerBox(modifier = Modifier.size(20.dp), alpha = alpha)
        }
    }
}

@Preview
@Composable
private fun ProfilePlaceholderPreview() {
    AppTheme {
        ProfilePlaceholder()
    }
}
