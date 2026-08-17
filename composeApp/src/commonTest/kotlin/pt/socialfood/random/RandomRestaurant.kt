package pt.socialfood.random

import pt.socialfood.domain.model.Restaurant
import kotlin.random.Random

fun Random.nextRestaurant(
    id: String = nextString(),
    name: String = nextString(),
    description: String? = nextNullable { nextString(20) },
    city: String = nextString(),
    country: String = nextString(),
    countryCode: String = nextString(2),
    postalCode: String? = nextNullable { nextString(6) },
    photoNames: List<String> = nextList { nextString() },
    address: String = nextString(20),
    rating: Double = nextDouble(0.0, 5.0),
    userRatingCount: Int = nextInt(0, 5_000),
    websiteUrl: String? = nextNullable { nextUrl() },
    phoneNumber: String = nextString(9),
    regularOpeningHours: List<String>? = nextNullable { nextList { nextString(12) } },
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
