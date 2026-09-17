package pt.socialfood.presentation.guide.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.ui.theme.PrivateBadge
import pt.socialfood.ui.theme.PublicBadge
import pt.socialfood.ui.theme.SharedBadge
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.author_icon
import socialfood.composeapp.generated.resources.guide_detail_private_icon_description
import socialfood.composeapp.generated.resources.guide_detail_public_icon_description
import socialfood.composeapp.generated.resources.guide_detail_shared_icon_description
import socialfood.composeapp.generated.resources.guide_private_icon
import socialfood.composeapp.generated.resources.guide_public_icon

@Composable
fun GuideVisibility.badgeIcon(): Painter = when (this) {
    GuideVisibility.PUBLIC -> painterResource(Res.drawable.guide_public_icon)
    GuideVisibility.PRIVATE -> painterResource(Res.drawable.guide_private_icon)
    GuideVisibility.SHARED -> painterResource(Res.drawable.author_icon)
}

fun GuideVisibility.badgeContentDescription(): StringResource = when (this) {
    GuideVisibility.PUBLIC -> Res.string.guide_detail_public_icon_description
    GuideVisibility.PRIVATE -> Res.string.guide_detail_private_icon_description
    GuideVisibility.SHARED -> Res.string.guide_detail_shared_icon_description
}

fun GuideVisibility.badgeBackgroundColor(): Color = when (this) {
    GuideVisibility.PUBLIC -> PublicBadge
    GuideVisibility.PRIVATE -> PrivateBadge
    GuideVisibility.SHARED -> SharedBadge
}
