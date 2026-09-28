package pt.socialfood.presentation.navigation

import androidx.navigation3.runtime.NavKey
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import pt.socialfood.presentation.author.navigation.AuthorRoute
import pt.socialfood.presentation.guide.navigation.GuideRoute
import pt.socialfood.presentation.home.navigation.HomeRoute
import pt.socialfood.presentation.search.navigation.SearchRoute
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.author_icon
import socialfood.composeapp.generated.resources.bottom_nav_authors
import socialfood.composeapp.generated.resources.bottom_nav_guides
import socialfood.composeapp.generated.resources.bottom_nav_home
import socialfood.composeapp.generated.resources.bottom_nav_search
import socialfood.composeapp.generated.resources.guide_icon
import socialfood.composeapp.generated.resources.home_icon
import socialfood.composeapp.generated.resources.search_icon

data class BottomNavItem(val icon: DrawableResource, val title: StringResource)

val TOP_LEVEL_DESTINATIONS: Map<NavKey, BottomNavItem> = mapOf(
    HomeRoute.Home to BottomNavItem(
        icon = Res.drawable.home_icon,
        title = Res.string.bottom_nav_home,
    ),
    SearchRoute.Search to BottomNavItem(
        icon = Res.drawable.search_icon,
        title = Res.string.bottom_nav_search,
    ),
    GuideRoute.Guides to BottomNavItem(
        icon = Res.drawable.guide_icon,
        title = Res.string.bottom_nav_guides,
    ),
    AuthorRoute.Authors to BottomNavItem(
        icon = Res.drawable.author_icon,
        title = Res.string.bottom_nav_authors,
    ),
)
