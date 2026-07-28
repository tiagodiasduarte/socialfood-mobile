package pt.socialfood.domain.use_case.favourite.guide

import kotlinx.coroutines.flow.Flow

interface ObserveFavouriteGuideIdsUseCase {
    operator fun invoke(): Flow<Set<String>>
}
