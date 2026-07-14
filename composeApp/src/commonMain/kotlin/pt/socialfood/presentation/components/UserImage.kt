package pt.socialfood.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import pt.socialfood.ui.theme.AppTypography

@Composable
fun UserImage(name: String, imageUrl: String?, imageSize: Dp) {
    val initials = name
        .trim()
        .split(" ")
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }

    Box(
        modifier = Modifier
            .size(imageSize)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF05A1A), Color(0xFFB82010)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl != null) {
            KamelImage(
                resource = asyncPainterResource(imageUrl),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(imageSize),
                onLoading = {
                    Text(
                        text = initials,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                onFailure = {
                    Text(
                        text = initials,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
            )
        } else {
            Text(
                text = initials,
                color = Color.White,
                style = AppTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}
