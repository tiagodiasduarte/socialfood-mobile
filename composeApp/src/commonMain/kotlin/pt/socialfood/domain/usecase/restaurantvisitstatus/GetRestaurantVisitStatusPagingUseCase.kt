package pt.socialfood.domain.usecase.restaurantvisitstatus

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.model.VisitStatus

interface GetRestaurantVisitStatusPagingUseCase {
    operator fun invoke(status: VisitStatus): Flow<PagingData<RestaurantVisitStatus>>
}
