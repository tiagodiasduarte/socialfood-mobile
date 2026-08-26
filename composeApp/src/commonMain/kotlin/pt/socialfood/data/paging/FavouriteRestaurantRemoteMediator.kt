package pt.socialfood.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import androidx.sqlite.SQLiteException
import pt.socialfood.core.Result
import pt.socialfood.data.api.FavouriteRestaurantsApi
import pt.socialfood.data.currentTimeMillis
import pt.socialfood.data.local.AppDatabase
import pt.socialfood.data.local.dao.FavouriteRestaurantDao
import pt.socialfood.data.local.dao.FavouriteRestaurantRemoteKeyDao
import pt.socialfood.data.local.entity.FavouriteRestaurantEntity
import pt.socialfood.data.local.entity.FavouriteRestaurantRemoteKeyEntity
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.error.toThrowable
import pt.socialfood.mapper.toFavouriteRestaurantEntity
import pt.socialfood.mapper.toRestaurant

const val FAVOURITE_RESTAURANTS_SCOPE = "favourite_restaurants"

fun interface FavouriteRestaurantCacheTransactionRunner {
    suspend fun run(block: suspend () -> Unit)
}

fun AppDatabase.asFavouriteRestaurantCacheTransactionRunner(): FavouriteRestaurantCacheTransactionRunner =
    FavouriteRestaurantCacheTransactionRunner { block ->
        useWriterConnection { transactor -> transactor.immediateTransaction { block() } }
    }

@OptIn(ExperimentalPagingApi::class)
class FavouriteRestaurantRemoteMediator(
    private val favouritesApi: FavouriteRestaurantsApi,
    private val favouriteDao: FavouriteRestaurantDao,
    private val remoteKeyDao: FavouriteRestaurantRemoteKeyDao,
    private val transactionRunner: FavouriteRestaurantCacheTransactionRunner,
) : RemoteMediator<Int, FavouriteRestaurantEntity>() {

    @Suppress("ReturnCount")
    override suspend fun load(loadType: LoadType, state: PagingState<Int, FavouriteRestaurantEntity>): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = remoteKeyDao.getByScope(FAVOURITE_RESTAURANTS_SCOPE)
                    remoteKey?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val limit = state.config.pageSize

            when (
                val result = safeApiCall { favouritesApi.findFavouriteRestaurants(page = page, limit = limit) }
            ) {
                is Result.Failure -> MediatorResult.Error(result.error.toThrowable())
                is Result.Success<PagedResponse<RestaurantResponse>> ->
                    applyResponse(result.data, loadType, page, limit)
            }
        } catch (e: SQLiteException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun applyResponse(
        response: PagedResponse<RestaurantResponse>,
        loadType: LoadType,
        page: Int,
        limit: Int,
    ): MediatorResult {
        val endOfPaginationReached = page * response.limit >= response.total
        val nextPage = if (endOfPaginationReached) null else page + 1
        val now = currentTimeMillis()

        transactionRunner.run {
            if (loadType == LoadType.REFRESH) {
                favouriteDao.deleteAll()
                remoteKeyDao.deleteByScope(FAVOURITE_RESTAURANTS_SCOPE)
            }
            val entities = response.items.mapIndexed { index, restaurantResponse ->
                restaurantResponse.toRestaurant().toFavouriteRestaurantEntity(
                    favouritedAt = now,
                    syncState = FavouriteSyncState.SYNCED,
                    position = (page - 1) * limit + index,
                )
            }
            favouriteDao.upsertAll(entities)
            remoteKeyDao.upsert(
                FavouriteRestaurantRemoteKeyEntity(
                    scope = FAVOURITE_RESTAURANTS_SCOPE,
                    nextPage = nextPage,
                    endOfPaginationReached = endOfPaginationReached,
                ),
            )
        }

        return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
    }
}
