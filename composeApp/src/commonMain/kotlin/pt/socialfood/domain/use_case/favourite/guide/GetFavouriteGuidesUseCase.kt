package pt.socialfood.domain.use_case.favourite.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedFavouriteGuides

interface GetFavouriteGuidesUseCase {
    suspend operator fun invoke(page: Int, limit: Int): Result<PagedFavouriteGuides>
}
