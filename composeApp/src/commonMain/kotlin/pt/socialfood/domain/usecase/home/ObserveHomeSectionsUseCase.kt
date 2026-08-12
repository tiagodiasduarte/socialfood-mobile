package pt.socialfood.domain.usecase.home

import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.HomeSection

interface ObserveHomeSectionsUseCase {
    operator fun invoke(): Flow<List<HomeSection>>
}
