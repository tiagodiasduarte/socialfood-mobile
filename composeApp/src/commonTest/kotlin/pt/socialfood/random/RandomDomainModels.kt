package pt.socialfood.random

import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.domain.model.Configs
import pt.socialfood.domain.model.Event
import pt.socialfood.domain.model.FavouriteGuide
import pt.socialfood.domain.model.FavouriteRestaurant
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.HomeItemType
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.model.HomeSectionItem
import pt.socialfood.domain.model.HomeSectionType
import pt.socialfood.domain.model.PagedAuthors
import pt.socialfood.domain.model.PagedFavouriteGuides
import pt.socialfood.domain.model.PagedFavouriteRestaurants
import pt.socialfood.domain.model.PagedGuides
import pt.socialfood.domain.model.PagedRestaurants
import pt.socialfood.domain.model.PagedUsers
import pt.socialfood.domain.model.Place
import pt.socialfood.domain.model.PresignedUrlData
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.User
import pt.socialfood.domain.model.UserRole

fun randomAuthor(
    id: String = randomId("author"),
    name: String = randomString(),
    username: String = randomString(),
    imageUrl: String? = randomNullable { randomUrl() },
) = Author(id = id, name = name, username = username, imageUrl = imageUrl)

fun randomAuthorDetail(
    id: String = randomId("author"),
    name: String = randomString(),
    username: String = randomString(),
    imageUrl: String? = randomNullable { randomUrl() },
    guidesCount: Int = randomInt(0, 100),
    followersCount: Int = randomInt(0, 10_000),
    followingCount: Int = randomInt(0, 10_000),
    facebookUrl: String? = randomNullable { randomUrl() },
    instagramUrl: String? = randomNullable { randomUrl() },
    youtubeUrl: String? = randomNullable { randomUrl() },
    guides: List<AuthorDetail.Guide> = randomList { randomAuthorDetailGuide() },
) = AuthorDetail(
    id = id,
    name = name,
    username = username,
    imageUrl = imageUrl,
    guidesCount = guidesCount,
    followersCount = followersCount,
    followingCount = followingCount,
    facebookUrl = facebookUrl,
    instagramUrl = instagramUrl,
    youtubeUrl = youtubeUrl,
    guides = guides,
)

fun randomAuthorDetailGuide(
    id: String = randomId("guide"),
    imageUrl: String? = randomNullable { randomUrl() },
    name: String = randomString(),
    description: String = randomString(),
    numberOfRestaurant: Int = randomInt(0, 50),
) = AuthorDetail.Guide(
    id = id,
    imageUrl = imageUrl,
    name = name,
    description = description,
    numberOfRestaurant = numberOfRestaurant,
)

fun randomConfigs(version: String = "${randomInt(1, 9)}.${randomInt(0, 9)}.${randomInt(0, 9)}") =
    Configs(version = version)

fun randomEvent(
    id: String = randomId("event"),
    name: String = randomString(),
    description: String = randomString(),
    imageUrl: String? = randomNullable { randomUrl() },
) = Event(id = id, name = name, description = description, imageUrl = imageUrl)

fun randomFavouriteGuide(guide: Guide = randomGuide(), favouritedAt: Long = randomLong()) =
    FavouriteGuide(guide = guide, favouritedAt = favouritedAt)

fun randomFavouriteRestaurant(restaurant: Restaurant = randomRestaurant(), favouritedAt: Long = randomLong()) =
    FavouriteRestaurant(restaurant = restaurant, favouritedAt = favouritedAt)

fun randomGuide(
    id: String = randomId("guide"),
    name: String = randomString(),
    description: String = randomString(),
    visibility: GuideVisibility = randomEnum(),
    author: Author = randomAuthor(),
    numberOfRestaurant: Int = randomInt(0, 50),
    restaurants: List<Restaurant> = emptyList(),
    imageUrl: String? = randomNullable { randomUrl() },
) = Guide(
    id = id,
    name = name,
    description = description,
    visibility = visibility,
    author = author,
    numberOfRestaurant = numberOfRestaurant,
    restaurants = restaurants,
    imageUrl = imageUrl,
)

fun randomHomeSection(
    id: String = randomId("section"),
    title: String = randomString(),
    type: HomeSectionType = randomEnum(),
    position: Int = randomInt(0, 10),
    isActive: Boolean = randomBoolean(),
    items: List<HomeSectionItem> = emptyList(),
) = HomeSection(
    id = id,
    title = title,
    type = type,
    position = position,
    isActive = isActive,
    items = items,
)

fun randomHomeSectionItem(
    id: String = randomId("item"),
    sectionId: String = randomId("section"),
    itemId: String = randomId("item"),
    itemType: HomeItemType = randomEnum(),
    position: Int = randomInt(0, 10),
    restaurant: Restaurant? = null,
    guide: Guide? = null,
    event: Event? = null,
) = HomeSectionItem(
    id = id,
    sectionId = sectionId,
    itemId = itemId,
    itemType = itemType,
    position = position,
    restaurant = restaurant,
    guide = guide,
    event = event,
)

fun randomPagedAuthors(
    authors: List<Author> = randomList { randomAuthor() },
    page: Int = randomInt(1, 10),
    hasMore: Boolean = randomBoolean(),
) = PagedAuthors(authors = authors, page = page, hasMore = hasMore)

fun randomPagedFavouriteGuides(
    favourites: List<FavouriteGuide> = randomList { randomFavouriteGuide() },
    page: Int = randomInt(1, 10),
    total: Int = favourites.size,
    hasMore: Boolean = randomBoolean(),
) = PagedFavouriteGuides(favourites = favourites, page = page, total = total, hasMore = hasMore)

fun randomPagedFavouriteRestaurants(
    favourites: List<FavouriteRestaurant> = randomList { randomFavouriteRestaurant() },
    page: Int = randomInt(1, 10),
    total: Int = favourites.size,
    hasMore: Boolean = randomBoolean(),
) = PagedFavouriteRestaurants(favourites = favourites, page = page, total = total, hasMore = hasMore)

fun randomPagedGuides(
    guides: List<Guide> = randomList { randomGuide() },
    page: Int = randomInt(1, 10),
    total: Int = guides.size,
    hasMore: Boolean = randomBoolean(),
) = PagedGuides(guides = guides, page = page, total = total, hasMore = hasMore)

fun randomPagedRestaurants(
    restaurants: List<Restaurant> = randomList { randomRestaurant() },
    page: Int = randomInt(1, 10),
    total: Int = restaurants.size,
    hasMore: Boolean = randomBoolean(),
) = PagedRestaurants(restaurants = restaurants, page = page, total = total, hasMore = hasMore)

fun randomPagedUsers(
    users: List<User> = randomList { randomUser() },
    page: Int = randomInt(1, 10),
    total: Int = users.size,
    hasMore: Boolean = randomBoolean(),
) = PagedUsers(users = users, page = page, total = total, hasMore = hasMore)

fun randomPlace(
    id: String = randomId("place"),
    name: String = randomString(),
    address: String = randomString(12),
    imageUrl: String? = randomNullable { randomUrl() },
) = Place(id = id, name = name, address = address, imageUrl = imageUrl)

fun randomPresignedUrlData(uploadUrl: String = randomUrl(), publicUrl: String = randomUrl()) =
    PresignedUrlData(uploadUrl = uploadUrl, publicUrl = publicUrl)

fun randomRestaurant(
    id: String = randomId("restaurant"),
    name: String = randomString(),
    description: String? = randomNullable { randomString(20) },
    city: String = randomString(),
    country: String = randomString(),
    countryCode: String = randomString(2),
    postalCode: String? = randomNullable { randomString(6) },
    photoNames: List<String> = randomList { randomString() },
    address: String = randomString(20),
    rating: Double = randomDouble(0.0, 5.0),
    userRatingCount: Int = randomInt(0, 5_000),
    websiteUrl: String? = randomNullable { randomUrl() },
    phoneNumber: String = randomString(9),
    regularOpeningHours: List<String>? = randomNullable { randomList { randomString(12) } },
) = Restaurant(
    id = id,
    name = name,
    description = description,
    city = city,
    country = country,
    countryCode = countryCode,
    postalCode = postalCode,
    photoNames = photoNames,
    address = address,
    rating = rating,
    userRatingCount = userRatingCount,
    websiteUrl = websiteUrl,
    phoneNumber = phoneNumber,
    regularOpeningHours = regularOpeningHours,
)

fun randomUser(
    id: String = randomId("user"),
    email: String = randomEmail(),
    name: String = randomString(),
    username: String = randomString(),
    imageUrl: String? = randomNullable { randomUrl() },
    role: UserRole = randomEnum(),
    city: String? = randomNullable { randomString() },
    country: String? = randomNullable { randomString() },
    facebookUrl: String? = randomNullable { randomUrl() },
    instagramUrl: String? = randomNullable { randomUrl() },
    youtubeUrl: String? = randomNullable { randomUrl() },
    isVerified: Boolean = randomBoolean(),
) = User(
    id = id,
    email = email,
    name = name,
    username = username,
    imageUrl = imageUrl,
    role = role,
    city = city,
    country = country,
    facebookUrl = facebookUrl,
    instagramUrl = instagramUrl,
    youtubeUrl = youtubeUrl,
    isVerified = isVerified,
)
