package pt.socialfood.presentation.search

import org.jetbrains.compose.resources.StringResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.search_suggestion_favorite_guides
import socialfood.composeapp.generated.resources.search_suggestion_favorite_restaurants

enum class SuggestionSource {
    RESTAURANTS,
    GUIDES,
}

fun SuggestionSource.labelRes(): StringResource = when (this) {
    SuggestionSource.RESTAURANTS -> Res.string.search_suggestion_favorite_restaurants
    SuggestionSource.GUIDES -> Res.string.search_suggestion_favorite_guides
}
