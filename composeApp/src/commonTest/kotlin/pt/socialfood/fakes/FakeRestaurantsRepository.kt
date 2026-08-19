package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedRestaurants
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.RestaurantsRepository
import pt.socialfood.random.nextPagedRestaurants
import pt.socialfood.random.nextRestaurant
import kotlin.random.Random

class FakeRestaurantsRepository(
    private val importRestaurantsResult: Result<Boolean> = Result.Success(true),
    private val deleteResult: Result<Boolean> = Result.Success(true),
    private val findAllResult: Result<List<Restaurant>> = Result.Success(emptyList()),
    private val findRestaurantsResult: Result<PagedRestaurants> = Result.Success(Random.nextPagedRestaurants()),
    private val findByIdResult: Result<Restaurant> = Result.Success(Random.nextRestaurant()),
    private val findByPlaceIdResult: Result<Restaurant> = Result.Success(Random.nextRestaurant()),
    private val addByPlaceIdResult: Result<Unit> = Result.Success(Unit),
    private val awaitEnrichedResult: Result<Restaurant> = Result.Success(Random.nextRestaurant()),
    private val updateResult: Result<Restaurant> = Result.Success(Random.nextRestaurant()),
) : RestaurantsRepository {
    var deleteInvokeCount: Int = 0
        private set
    var lastDeleteId: String? = null
        private set

    var lastFindRestaurantsPage: Int? = null
        private set
    var lastFindRestaurantsLimit: Int? = null
        private set
    var lastFindRestaurantsQuery: String? = null
        private set

    var lastFindByIdId: String? = null
        private set

    var lastFindByPlaceIdPlaceId: String? = null
        private set

    var addByPlaceIdInvokeCount: Int = 0
        private set
    var lastAddByPlaceIdPlaceId: String? = null
        private set

    var awaitEnrichedInvokeCount: Int = 0
        private set
    var lastAwaitEnrichedPlaceId: String? = null
        private set

    var updateInvokeCount: Int = 0
        private set
    var lastUpdateId: String? = null
        private set
    var lastUpdateName: String? = null
        private set
    var lastUpdateDescription: String? = null
        private set
    var lastUpdateCountry: String? = null
        private set
    var lastUpdateCity: String? = null
        private set
    var lastUpdateAddress: String? = null
        private set
    var lastUpdatePhoneNumber: String? = null
        private set
    var lastUpdateWebsiteUrl: String? = null
        private set

    override suspend fun importRestaurants(): Result<Boolean> = importRestaurantsResult

    override suspend fun delete(id: String): Result<Boolean> {
        deleteInvokeCount++
        lastDeleteId = id
        return deleteResult
    }

    override suspend fun findAll(): Result<List<Restaurant>> = findAllResult

    override suspend fun findRestaurants(page: Int, limit: Int, query: String?): Result<PagedRestaurants> {
        lastFindRestaurantsPage = page
        lastFindRestaurantsLimit = limit
        lastFindRestaurantsQuery = query
        return findRestaurantsResult
    }

    override suspend fun findById(id: String): Result<Restaurant> {
        lastFindByIdId = id
        return findByIdResult
    }

    override suspend fun findByPlaceId(placeId: String): Result<Restaurant> {
        lastFindByPlaceIdPlaceId = placeId
        return findByPlaceIdResult
    }

    override suspend fun addByPlaceId(placeId: String): Result<Unit> {
        addByPlaceIdInvokeCount++
        lastAddByPlaceIdPlaceId = placeId
        return addByPlaceIdResult
    }

    override suspend fun awaitEnrichedRestaurantByPlaceId(placeId: String): Result<Restaurant> {
        awaitEnrichedInvokeCount++
        lastAwaitEnrichedPlaceId = placeId
        return awaitEnrichedResult
    }

    override suspend fun update(
        id: String,
        name: String,
        description: String?,
        country: String,
        city: String,
        address: String,
        phoneNumber: String,
        websiteUrl: String,
    ): Result<Restaurant> {
        updateInvokeCount++
        lastUpdateId = id
        lastUpdateName = name
        lastUpdateDescription = description
        lastUpdateCountry = country
        lastUpdateCity = city
        lastUpdateAddress = address
        lastUpdatePhoneNumber = phoneNumber
        lastUpdateWebsiteUrl = websiteUrl
        return updateResult
    }
}
