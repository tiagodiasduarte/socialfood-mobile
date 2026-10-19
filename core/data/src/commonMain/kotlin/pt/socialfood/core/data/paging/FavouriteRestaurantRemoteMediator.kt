package pt.socialfood.core.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import androidx.sqlite.SQLiteException
import pt.socialfood.core.common.Result
import pt.socialfood.core.common.error.toThrowable
import pt.socialfood.core.data.currentTimeMillis
import pt.socialfood.core.data.mapper.toFavouriteRestaurantEntity
import pt.socialfood.core.data.mapper.toRestaurant
import pt.socialfood.core.database.AppDatabase
import pt.socialfood.core.database.dao.FavouriteRestaurantDao
import pt.socialfood.core.database.dao.FavouriteRestaurantRemoteKeyDao
import pt.socialfood.core.database.entity.FavouriteRestaurantEntity
import pt.socialfood.core.database.entity.FavouriteRestaurantRemoteKeyEntity
import pt.socialfood.core.database.entity.FavouriteSyncState
import pt.socialfood.core.network.api.FavouriteRestaurantsApi
import pt.socialfood.core.network.model.PagedResponse
import pt.socialfood.core.network.model.restaurant.RestaurantResponse
import pt.socialfood.core.network.safeApiCall

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
                val result = safeApiCall { favouritesApi.find(page = page, limit = limit) }
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
