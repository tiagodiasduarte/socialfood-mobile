package pt.socialfood.presentation.author.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import pt.socialfood.presentation.author.detail.AuthorDetailScreen
import pt.socialfood.presentation.author.list.AuthorsScreen
import pt.socialfood.presentation.guide.navigation.GuideRoute
import pt.socialfood.presentation.navigation.Navigator
import pt.socialfood.presentation.navigation.defaultAnimationMetadata
import pt.socialfood.presentation.navigation.slideHorizontalAnimationMetadata
import pt.socialfood.presentation.profile.navigation.ProfileRoute

fun EntryProviderScope<NavKey>.authorEntries(navigator: Navigator, onOpenDrawer: () -> Unit) {
    entry<AuthorRoute.AuthorDetail>(metadata = defaultAnimationMetadata) { route ->
        AuthorDetailScreen(
            authorId = route.authorId,
            onBackClick = navigator::goBack,
            onGuideClick = { guideId -> navigator.navigate(GuideRoute.GuideDetail(guideId)) },
        )
    }
    entry<AuthorRoute.Authors>(metadata = defaultAnimationMetadata) {
        AuthorsScreen(
            onAuthorClick = { authorId -> navigator.navigate(AuthorRoute.AuthorDetail(authorId)) },
            onProfileClick = onOpenDrawer,
        )
    }
    entry<AuthorRoute.Profile>(metadata = slideHorizontalAnimationMetadata) { route ->
        AuthorDetailScreen(
            authorId = route.authorId,
            isOwnProfile = true,
            onBackClick = navigator::goBack,
            onGuideClick = { guideId -> navigator.navigate(GuideRoute.GuideDetail(guideId)) },
            onEditProfileClick = { navigator.navigate(ProfileRoute.EditProfile) },
        )
    }
}
