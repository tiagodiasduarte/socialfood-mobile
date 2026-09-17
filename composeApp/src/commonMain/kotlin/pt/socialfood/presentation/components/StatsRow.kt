package pt.socialfood.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.profile_stat_followers_label
import socialfood.composeapp.generated.resources.profile_stat_following_label
import socialfood.composeapp.generated.resources.profile_stat_guides_label

@Composable
fun StatsRow(
    textStyle: TextStyle = AppTypography.bodySmall,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        StatItem(
            value = "-",
            textStyle = textStyle,
            textColor = textColor,
            label = stringResource(Res.string.profile_stat_guides_label),
        )
        StatItem(
            value = "-",
            textStyle = textStyle,
            textColor = textColor,
            label = stringResource(Res.string.profile_stat_followers_label),
        )
        StatItem(
            value = "-",
            textStyle = textStyle,
            textColor = textColor,
            label = stringResource(Res.string.profile_stat_following_label),
        )
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    textColor: Color,
) {
    Row(modifier = modifier) {
        Text(text = value, style = textStyle.copy(fontWeight = FontWeight.Bold), color = textColor)
        Spacer(Modifier.width(SpaceSize.small))
        Text(text = label, style = textStyle, color = textColor.copy(alpha = 0.85f))
    }
}

@Composable
@Preview
fun StatsRowPreview() {
    AppTheme { StatsRow() }
}
