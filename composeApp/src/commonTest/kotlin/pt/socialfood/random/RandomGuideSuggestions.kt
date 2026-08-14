package pt.socialfood.random

import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideSuggestions
import kotlin.random.Random

fun Random.nextGuideSuggestions(guides: List<Guide> = nextList { nextGuide() }, generatedAt: String = nextString()) =
    GuideSuggestions(guides = guides, generatedAt = generatedAt)
