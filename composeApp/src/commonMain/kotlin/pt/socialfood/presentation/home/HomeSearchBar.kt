package pt.socialfood.presentation.home

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SearchBorder
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.home_search_bar_placeholder

@Composable
fun HomeSearchBar(searchQuery: String, onQueryChange: (String) -> Unit, onClick: () -> Unit = {}) {
    TextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        enabled = false,
        placeholder = {
            Text(
                stringResource(Res.string.home_search_bar_placeholder),
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
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .padding(horizontal = SpaceSize.large)
            .fillMaxWidth()
            .height(50.dp)
            .border(
                width = 1.dp,
                color = SearchBorder,
                shape = RoundedCornerShape(25),
            )
            .clickable(onClick = onClick),
    )
}
