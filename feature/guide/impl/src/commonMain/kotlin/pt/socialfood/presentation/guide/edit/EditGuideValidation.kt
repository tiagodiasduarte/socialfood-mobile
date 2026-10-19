package pt.socialfood.presentation.guide.edit

import org.jetbrains.compose.resources.StringResource
import pt.socialfood.core.model.GuideVisibility
import pt.socialfood.feature.guide.impl.generated.resources.Res
import pt.socialfood.feature.guide.impl.generated.resources.edit_guide_details_description_error
import pt.socialfood.feature.guide.impl.generated.resources.edit_guide_details_public_author_warning
import pt.socialfood.feature.guide.impl.generated.resources.edit_guide_details_public_image_warning
import pt.socialfood.feature.guide.impl.generated.resources.edit_guide_details_public_restaurants_warning
import pt.socialfood.feature.guide.impl.generated.resources.edit_guide_details_title_error

private const val MIN_PUBLIC_GUIDE_RESTAURANTS = 3

/** The reasons this guide can't be saved yet; empty when it can. */
internal fun EditGuideUiState.Loaded.validationErrors(): List<StringResource> = buildList {
    if (title.isBlank()) add(Res.string.edit_guide_details_title_error)
    if (description.isBlank()) add(Res.string.edit_guide_details_description_error)
    if (visibility == GuideVisibility.PUBLIC) {
        if (restaurants.size < MIN_PUBLIC_GUIDE_RESTAURANTS) {
            add(Res.string.edit_guide_details_public_restaurants_warning)
        }
        if (imageUrl == null && pendingImage == null) add(Res.string.edit_guide_details_public_image_warning)
        if (!isAuthor) add(Res.string.edit_guide_details_public_author_warning)
    }
}
