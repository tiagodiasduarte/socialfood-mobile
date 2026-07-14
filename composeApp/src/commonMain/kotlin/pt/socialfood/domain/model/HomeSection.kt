package pt.socialfood.domain.model

data class HomeSection(
    val id: String,
    val title: String,
    val type: HomeSectionType,
    val position: Int,
    val isActive: Boolean,
    val items: List<HomeSectionItem> = emptyList(),
)

data class HomeSectionItem(
    val id: String,
    val sectionId: String,
    val itemId: String,
    val itemType: HomeItemType,
    val position: Int,
    val restaurant: Restaurant? = null,
    val guide: Guide? = null,
    val event: Event? = null,
)

enum class HomeSectionType { RESTAURANT_LIST, GUIDE_LIST, EVENTS }

enum class HomeItemType { RESTAURANT, GUIDE, EVENT }