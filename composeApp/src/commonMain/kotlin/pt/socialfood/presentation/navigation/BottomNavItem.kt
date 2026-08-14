package pt.socialfood.presentation.navigation

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.author_icon
import socialfood.composeapp.generated.resources.bottom_nav_authors
import socialfood.composeapp.generated.resources.bottom_nav_guides
import socialfood.composeapp.generated.resources.bottom_nav_home
import socialfood.composeapp.generated.resources.bottom_nav_search
import socialfood.composeapp.generated.resources.guides_icon
import socialfood.composeapp.generated.resources.home_icon
import socialfood.composeapp.generated.resources.search_icon

data class BottomNavItem(val icon: DrawableResource, val title: StringResource)

val TOP_LEVEL_DESTINATIONS: Map<Route, BottomNavItem> = mapOf(
    Route.Home to BottomNavItem(
        icon = Res.drawable.home_icon,
        title = Res.string.bottom_nav_home,
    ),
    Route.Search to BottomNavItem(
        icon = Res.drawable.search_icon,
        title = Res.string.bottom_nav_search,
    ),
    Route.Guides to BottomNavItem(
        icon = Res.drawable.guides_icon,
        title = Res.string.bottom_nav_guides,
    ),
    Route.Authors to BottomNavItem(
        icon = Res.drawable.author_icon,
        title = Res.string.bottom_nav_authors,
    ),
)
