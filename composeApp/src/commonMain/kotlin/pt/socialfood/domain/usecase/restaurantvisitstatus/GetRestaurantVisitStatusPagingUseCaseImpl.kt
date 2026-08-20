package pt.socialfood.domain.usecase.restaurantvisitstatus

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository

class GetRestaurantVisitStatusPagingUseCaseImpl(private val repository: RestaurantVisitStatusRepository) :
    GetRestaurantVisitStatusPagingUseCase {
    override operator fun invoke(status: VisitStatus): Flow<PagingData<RestaurantVisitStatus>> =
        repository.getPagingFlow(status)
}
