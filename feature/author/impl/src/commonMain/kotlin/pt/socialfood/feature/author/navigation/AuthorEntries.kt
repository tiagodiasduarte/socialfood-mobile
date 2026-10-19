package pt.socialfood.feature.author.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import pt.socialfood.core.navigation.Navigator
import pt.socialfood.core.navigation.defaultAnimationMetadata
import pt.socialfood.core.navigation.slideHorizontalAnimationMetadata
import pt.socialfood.feature.author.detail.AuthorDetailScreen
import pt.socialfood.feature.author.list.AuthorsScreen
import pt.socialfood.presentation.guide.navigation.GuideRoute
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
