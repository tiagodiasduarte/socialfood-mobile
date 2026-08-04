package pt.socialfood.random

import pt.socialfood.domain.model.Restaurant

@Suppress("LongParameterList")
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
