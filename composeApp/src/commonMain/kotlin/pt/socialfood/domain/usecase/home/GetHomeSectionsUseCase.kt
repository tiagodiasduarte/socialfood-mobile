package pt.socialfood.domain.usecase.home

import pt.socialfood.core.Result
import pt.socialfood.domain.model.HomeSection

interface GetHomeSectionsUseCase {
    suspend operator fun invoke(): Result<List<HomeSection>>
}
