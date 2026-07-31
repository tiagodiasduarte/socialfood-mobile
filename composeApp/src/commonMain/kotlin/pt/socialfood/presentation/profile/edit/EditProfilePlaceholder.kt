package pt.socialfood.presentation.profile.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
internal fun EditProfilePlaceholder() {
    val alpha = rememberShimmerAlpha()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(GreyBackground),
    ) {
        // Top bar skeleton
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = SpaceSize.medium, vertical = SpaceSize.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerBox(modifier = Modifier.size(40.dp), alpha = alpha, shape = CircleShape)
            Spacer(Modifier.weight(1f))
            ShimmerBox(modifier = Modifier.width(80.dp).height(20.dp), alpha = alpha)
            Spacer(Modifier.weight(1f))
            ShimmerBox(
                modifier = Modifier.width(64.dp).height(36.dp),
                alpha = alpha,
                shape = RoundedCornerShape(SpaceSize.large),
            )
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(SpaceSize.large),
            verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            // Profile picture card
            SectionCardPlaceholder {
                ShimmerBox(modifier = Modifier.width(140.dp).height(20.dp), alpha = alpha)
                Spacer(Modifier.height(SpaceSize.large))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
                ) {
                    ShimmerBox(modifier = Modifier.size(72.dp), alpha = alpha, shape = CircleShape)
                    Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.medium)) {
                        ShimmerBox(modifier = Modifier.width(180.dp).height(14.dp), alpha = alpha)
                        ShimmerBox(modifier = Modifier.width(100.dp).height(14.dp), alpha = alpha)
                    }
                }
            }

            // Personal details card
            SectionCardPlaceholder {
                ShimmerBox(modifier = Modifier.width(140.dp).height(20.dp), alpha = alpha)
                Spacer(Modifier.height(SpaceSize.large))
                repeat(3) {
                    FieldPlaceholder(alpha = alpha)
                    if (it < 2) Spacer(Modifier.height(SpaceSize.large))
                }
            }

            // Social networks card
            SectionCardPlaceholder {
                ShimmerBox(modifier = Modifier.width(140.dp).height(20.dp), alpha = alpha)
                Spacer(Modifier.height(SpaceSize.large))
                repeat(3) {
                    SocialFieldPlaceholder(alpha = alpha)
                    if (it < 2) Spacer(Modifier.height(SpaceSize.large))
                }
            }

            Spacer(Modifier.height(SpaceSize.large))
        }
    }
}

@Composable
private fun SectionCardPlaceholder(
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SpaceSize.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(SpaceSize.large)) {
            content()
        }
    }
}

@Composable
private fun FieldPlaceholder(alpha: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.small)) {
        ShimmerBox(modifier = Modifier.width(80.dp).height(12.dp), alpha = alpha)
        ShimmerBox(
            modifier = Modifier.fillMaxWidth().height(52.dp),
            alpha = alpha,
            shape = RoundedCornerShape(SpaceSize.medium),
        )
    }
}

@Composable
private fun SocialFieldPlaceholder(alpha: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.small)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.small),
        ) {
            ShimmerBox(modifier = Modifier.size(18.dp), alpha = alpha, shape = CircleShape)
            ShimmerBox(modifier = Modifier.width(70.dp).height(12.dp), alpha = alpha)
        }
        ShimmerBox(
            modifier = Modifier.fillMaxWidth().height(52.dp),
            alpha = alpha,
            shape = RoundedCornerShape(SpaceSize.medium),
        )
    }
}

@Preview
@Composable
private fun EditProfilePlaceholderPreview() {
    AppTheme {
        EditProfilePlaceholder()
    }
}
