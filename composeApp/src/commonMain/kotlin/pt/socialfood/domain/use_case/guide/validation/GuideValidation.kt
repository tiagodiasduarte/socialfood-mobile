package pt.socialfood.domain.use_case.guide.validation

import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.GuideVisibility

internal fun validateGuideInput(
    title: String,
    description: String,
    visibility: GuideVisibility,
    restaurantIds: List<String>? = null,
    requireDescription: Boolean = true,
): ErrorEntity.Validation? {
    if (title.isBlank()) return ErrorEntity.Validation.EmptyTitle
    if (requireDescription && description.isBlank()) return ErrorEntity.Validation.EmptyDescription
    if (visibility == GuideVisibility.PUBLIC && restaurantIds!= null && restaurantIds.size < 3) {
        return ErrorEntity.Validation.PublicGuideNeedsMoreRestaurants
    }
    return null
}
