package pt.socialfood.feature.profile.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import pt.socialfood.core.navigation.Navigator
import pt.socialfood.core.navigation.slideHorizontalAnimationMetadata
import pt.socialfood.feature.profile.edit.EditProfileScreen

fun EntryProviderScope<NavKey>.profileEntries(navigator: Navigator) {
    entry<ProfileRoute.EditProfile>(metadata = slideHorizontalAnimationMetadata) {
        EditProfileScreen(onBackClick = navigator::goBack)
    }
}
