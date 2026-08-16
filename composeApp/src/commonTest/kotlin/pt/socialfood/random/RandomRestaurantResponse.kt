package pt.socialfood.random

import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import kotlin.random.Random

fun Random.nextRestaurantResponse(
    id: String = nextString(),
    name: String = nextString(),
    description: String? = nextNullable { nextString(20) },
    photoNames: List<String> = nextList { nextString() },
    city: String = nextString(),
    country: String = nextString(),
    countryCode: String = nextString(2),
    postalCode: String? = nextNullable { nextString(6) },
    phoneNumber: String = nextString(9),
    address: String = nextString(20),
    rating: Double = nextDouble(0.0, 5.0),
    userRatingCount: Int = nextInt(0, 5_000),
    websiteUrl: String? = nextNullable { nextUrl() },
    location: RestaurantResponse.Location = RestaurantResponse.Location(
        latitude = nextDouble(-90.0, 90.0),
        longitude = nextDouble(-180.0, 180.0),
    ),
    regularOpeningHours: List<String>? = nextNullable { nextList { nextString(12) } },
) = RestaurantResponse(
    id = id,
    name = name,
    description = description,
    photoNames = photoNames,
    city = city,
    country = country,
    countryCode = countryCode,
    postalCode = postalCode,
    phoneNumber = phoneNumber,
    address = address,
    rating = rating,
    userRatingCount = userRatingCount,
    websiteUrl = websiteUrl,
    location = location,
    regularOpeningHours = regularOpeningHours,
)
