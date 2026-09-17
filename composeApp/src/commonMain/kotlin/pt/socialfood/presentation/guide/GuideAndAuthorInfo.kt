package pt.socialfood.presentation.guide

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.socialfood.domain.model.Guide
import pt.socialfood.presentation.guide.shared.AuthorChip
import pt.socialfood.ui.theme.AppTypography

@Composable
fun GuideAndAuthorInfo(guide: Guide, fontColor: Color = Color.White.copy(alpha = 0.9f), modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "${guide.numberOfRestaurant} restaurants",
            style = AppTypography.labelMedium,
            color = fontColor,
        )

        Text(
            text = "•",
            style = AppTypography.labelMedium,
            color = fontColor,
        )
        AuthorChip(author = guide.author, fontColor = fontColor)
    }
}
