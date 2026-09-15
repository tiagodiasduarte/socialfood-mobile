package pt.socialfood.domain.usecase.restaurantvisitstatus

import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository

class GetRestaurantVisitStatusListUseCaseImpl(private val repository: RestaurantVisitStatusRepository) :
    GetRestaurantVisitStatusListUseCase {
    override operator fun invoke(status: VisitStatus): Flow<List<Restaurant>> = repository.getAllFlow(status)
}
