package pt.socialfood.mapper

import pt.socialfood.data.network.model.home.HomeSectionItemResponse
import pt.socialfood.data.network.model.home.HomeSectionResponse
import pt.socialfood.domain.model.HomeItemType
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.model.HomeSectionItem
import pt.socialfood.domain.model.HomeSectionType

fun HomeSectionResponse.toHomeSection(): HomeSection =
    HomeSection(
        id = id,
        title = title,
        type = runCatching { HomeSectionType.valueOf(type) }.getOrDefault(HomeSectionType.RESTAURANT_LIST),
        position = position,
        isActive = isActive,
        items = items.map { it.toHomeSectionItem() },
    )

fun HomeSectionItemResponse.toHomeSectionItem(): HomeSectionItem =
    HomeSectionItem(
        id = id,
        sectionId = sectionId,
        itemId = itemId,
        itemType = runCatching { HomeItemType.valueOf(itemType) }.getOrDefault(HomeItemType.RESTAURANT),
        position = position,
        restaurant = restaurant?.toRestaurant(),
        guide = guide?.toGuide(),
    )