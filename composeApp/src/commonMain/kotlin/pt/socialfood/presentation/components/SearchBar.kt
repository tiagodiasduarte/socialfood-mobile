package pt.socialfood.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun SearchBar(
    placeholder: String,
    searchQuery: String = "",
    onQueryChange: (String) -> Unit = {},
    enabled: Boolean = true,
) {
    TextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        enabled = enabled,
        placeholder = {
            Text(
                placeholder,
                style = AppTypography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp),
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(25),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = GreyBackground,
            focusedContainerColor = GreyBackground,
            disabledContainerColor = GreyBackground,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledLeadingIconColor = MaterialTheme.colorScheme.outline,
            disabledPlaceholderColor = MaterialTheme.colorScheme.outline,
        ),
        modifier = Modifier
            .padding(horizontal = SpaceSize.large)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(0xFFE5E7EB),
                shape = RoundedCornerShape(25)
            )
    )
}
