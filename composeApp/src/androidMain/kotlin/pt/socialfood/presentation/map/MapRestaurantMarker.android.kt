package pt.socialfood.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pt.socialfood.ui.theme.SpaceSize

private val PinTailWidth = 12.dp
private val PinTailHeight = 6.dp
private val PinBorderWidth = 1.dp

private val PinTailShape = GenericShape { size, _ ->
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width / 2f, size.height)
    close()
}

@Composable
internal fun MapRestaurantMarker(name: String, selected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(SpaceSize.large))
                .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                .border(PinBorderWidth, MaterialTheme.colorScheme.primary, RoundedCornerShape(SpaceSize.large))
                .padding(horizontal = SpaceSize.medium, vertical = SpaceSize.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
            )
        }
        Box(
            modifier = Modifier
                .size(width = PinTailWidth, height = PinTailHeight)
                .clip(PinTailShape)
                .background(MaterialTheme.colorScheme.primary),
        )
    }
}
