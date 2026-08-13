package pt.socialfood.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SpaceSize

@Composable
internal fun SearchSectionHeader(icon: ImageVector, title: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(horizontal = SpaceSize.large, vertical = SpaceSize.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.small),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp),
        )

        Text(
            text = title.uppercase(),
            style = AppTypography.labelLarge.copy(letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
