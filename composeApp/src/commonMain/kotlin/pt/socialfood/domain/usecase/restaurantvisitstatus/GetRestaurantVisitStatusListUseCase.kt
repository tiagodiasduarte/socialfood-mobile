package pt.socialfood.domain.usecase.restaurantvisitstatus

import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus

interface GetRestaurantVisitStatusListUseCase {
    operator fun invoke(status: VisitStatus): Flow<List<Restaurant>>
}
