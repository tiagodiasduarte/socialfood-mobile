package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.use_case.restaurant.GetRestaurantByIdUseCase

/**
 * Returns [results] in order, one per invocation; once exhausted, keeps repeating the
 * last item. Lets tests script a polling sequence (e.g. enriching -> enriching -> ready).
 */
class FakeGetRestaurantByIdUseCase(
    private val results: List<Result<Restaurant>>,
) : GetRestaurantByIdUseCase {
    var invokeCount: Int = 0
        private set

    override suspend operator fun invoke(id: String): Result<Restaurant> {
        val result = results.getOrElse(invokeCount) { results.last() }
        invokeCount++
        return result
    }
}
