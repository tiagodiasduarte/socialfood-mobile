package pt.socialfood.random

import pt.socialfood.domain.model.Event
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.HomeItemType
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.model.HomeSectionItem
import pt.socialfood.domain.model.HomeSectionType
import pt.socialfood.domain.model.Restaurant

@Suppress("LongParameterList")
fun randomHomeSection(
    id: String = randomString(),
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

@Suppress("LongParameterList")
fun randomHomeSectionItem(
    id: String = randomString(),
    sectionId: String = randomString(),
    itemId: String = randomString(),
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
