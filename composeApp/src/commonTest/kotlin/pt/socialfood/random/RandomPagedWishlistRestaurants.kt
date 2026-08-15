package pt.socialfood.random

import pt.socialfood.domain.model.PagedWishlistRestaurants
import pt.socialfood.domain.model.WishlistRestaurant
import kotlin.random.Random

fun Random.nextPagedWishlistRestaurants(
    wishlist: List<WishlistRestaurant> = nextList { nextWishlistRestaurant() },
    page: Int = nextInt(1, 10),
    total: Int = wishlist.size,
    hasMore: Boolean = nextBoolean(),
) = PagedWishlistRestaurants(wishlist = wishlist, page = page, total = total, hasMore = hasMore)
