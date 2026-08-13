package pt.socialfood.random

import pt.socialfood.domain.model.Event
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.HomeItemType
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.model.HomeSectionItem
import pt.socialfood.domain.model.HomeSectionType
import pt.socialfood.domain.model.Restaurant
import kotlin.random.Random

@Suppress("LongParameterList")
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

@Suppress("LongParameterList")
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
