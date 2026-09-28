package pt.socialfood.domain.usecase.favourite.guide

import kotlinx.coroutines.flow.Flow

interface ObserveFavouriteGuideIdsUseCase {
    operator fun invoke(): Flow<Set<String>>
}
