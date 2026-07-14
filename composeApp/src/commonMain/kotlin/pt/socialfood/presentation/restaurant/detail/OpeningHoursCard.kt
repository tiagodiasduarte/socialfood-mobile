package pt.socialfood.presentation.restaurant.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.restaurant_detail_day_friday
import socialfood.composeapp.generated.resources.restaurant_detail_day_monday
import socialfood.composeapp.generated.resources.restaurant_detail_day_saturday
import socialfood.composeapp.generated.resources.restaurant_detail_day_sunday
import socialfood.composeapp.generated.resources.restaurant_detail_day_thursday
import socialfood.composeapp.generated.resources.restaurant_detail_day_tuesday
import socialfood.composeapp.generated.resources.restaurant_detail_day_wednesday
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize


@Composable
fun OpeningHoursCard(
    openingHours: List<String>,
) {
    val map = openingHours.toOpeningHoursMap()
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = SpaceSize.large),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
            modifier = Modifier.padding(SpaceSize.large)
        ) {
            DayItem(
                weekDay = stringResource(Res.string.restaurant_detail_day_monday),
                value = map["Monday"]
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            DayItem(
                weekDay = stringResource(Res.string.restaurant_detail_day_tuesday),
                value = map["Tuesday"]
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            DayItem(
                weekDay = stringResource(Res.string.restaurant_detail_day_wednesday),
                value = map["Wednesday"]
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            DayItem(
                weekDay = stringResource(Res.string.restaurant_detail_day_thursday),
                value = map["Thursday"]
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            DayItem(
                weekDay = stringResource(Res.string.restaurant_detail_day_friday),
                value = map["Friday"]
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            DayItem(
                weekDay = stringResource(Res.string.restaurant_detail_day_saturday),
                value = map["Saturday"]
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            DayItem(
                weekDay = stringResource(Res.string.restaurant_detail_day_sunday),
                value = map["Sunday"]
            )
        }
    }
}

@Composable
private fun DayItem(weekDay: String, value: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = weekDay,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = value?.replace(", ", "\n") ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

private fun List<String>.toOpeningHoursMap(): Map<String, String> {
    return this.mapNotNull { entry ->
        val parts = entry.split(":", limit = 2)
        if (parts.size < 2) return@mapNotNull null

        parts[0].trim() to parts[1].trim()
    }.toMap()
}

@Preview
@Composable
private fun OpeningHoursCardPreview() {
    AppTheme {
        OpeningHoursCard(
            openingHours = listOf(
                "Monday: 12:00–15:00, 19:00–23:00",
                "Tuesday: 12:00–15:00, 19:00–23:00",
                "Wednesday: 12:00–15:00, 19:00–23:00",
                "Thursday: 12:00–15:00, 19:00–23:00",
                "Friday: 12:00–15:00, 19:00–23:30",
                "Saturday: 12:00–16:00, 19:00–23:30",
                "Sunday: Closed",
            )
        )
    }
}

