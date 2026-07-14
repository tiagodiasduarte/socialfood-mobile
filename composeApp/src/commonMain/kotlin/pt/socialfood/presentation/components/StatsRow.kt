package pt.socialfood.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun StatsRow(
    guidesCount: Int? = null,
    followersCount: Int? = null,
    followingCount: Int? = null,
) {
    Row(
        modifier = Modifier
            .padding(vertical = SpaceSize.large),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatItem(value = guidesCount?.toString() ?: "-", label = "Albums")
        Box(
            modifier = Modifier
                .padding(horizontal = SpaceSize.large)
                .size(width = 1.dp, height = 32.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
        StatItem(value = followersCount?.toString() ?: "-", label = "Followers")
        Box(
            modifier = Modifier
                .padding(horizontal = SpaceSize.large)
                .size(width = 1.dp, height = 32.dp)
                .background(MaterialTheme.colorScheme.outlineVariant),
        )
        StatItem(value = followingCount?.toString() ?: "-", label = "Following")
    }

}

@Composable
private fun StatItem(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpaceSize.small),
    ) {
        Text(
            text = value,
            style = AppTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = label,
            style = AppTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
@Preview
fun StatsRowPreview() {
    AppTheme { StatsRow() }
}