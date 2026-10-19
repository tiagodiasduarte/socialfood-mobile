package pt.socialfood.core.testing.random

import pt.socialfood.core.model.Event
import pt.socialfood.core.model.Guide
import pt.socialfood.core.model.HomeItemType
import pt.socialfood.core.model.HomeSection
import pt.socialfood.core.model.HomeSectionItem
import pt.socialfood.core.model.HomeSectionType
import pt.socialfood.core.model.Restaurant
import kotlin.random.Random

fun Random.nextHomeSection(
    id: String = nextString(),
    title: String = nextString(),
    type: HomeSectionType = nextEnum(),
    position: Int = nextInt(0, 10),
    isActive: Boolean = nextBoolean(),
    items: List<HomeSectionItem> = emptyList(),
) = HomeSection(
    id = id,
    title = title,
    type = type,
    position = position,
    isActive = isActive,
    items = items,
)

fun Random.nextHomeSectionItem(
    id: String = nextString(),
    sectionId: String = nextString(),
    itemId: String = nextString(),
    itemType: HomeItemType = nextEnum(),
    position: Int = nextInt(0, 10),
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
