package pt.socialfood.domain.repository

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedRestaurants
import pt.socialfood.domain.model.Restaurant


interface RestaurantsRepository {

    suspend fun importRestaurants(): Result<Boolean>
    suspend fun delete(id: String): Result<Boolean>
    suspend fun findAll(): Result<List<Restaurant>>
    suspend fun findRestaurants(page: Int, limit: Int, query: String? = null): Result<PagedRestaurants>
    suspend fun findById(id: String): Result<Restaurant>
    suspend fun findByPlaceId(placeId: String): Result<Restaurant>
    suspend fun addByPlaceId(placeId: String): Result<Unit>

    /**
     * Polls findByPlaceId until the backend reports the restaurant is done being
     * enriched (see APPS-16), or gives up after RestaurantEnrichmentPolling.MAX_POLL_ATTEMPTS
     * with Result.Error(ErrorEntity.Network.TIMEOUT). The "still enriching" signal only
     * exists on the raw network response, so this stays a repository-level concern —
     * callers never see a Restaurant that isn't already fully enriched.
     */
    suspend fun awaitEnrichedRestaurantByPlaceId(placeId: String): Result<Restaurant>
    suspend fun update(
        id: String,
        name: String,
        description: String?,
        country: String,
        city: String,
        address: String,
        phoneNumber: String,
        websiteUrl: String,
    ): Result<Restaurant>
}