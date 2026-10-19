package pt.socialfood.random

import pt.socialfood.core.model.Guide
import pt.socialfood.core.model.GuideSuggestions
import kotlin.random.Random

fun Random.nextGuideSuggestions(guides: List<Guide> = nextList { nextGuide() }, generatedAt: String = nextString()) =
    GuideSuggestions(guides = guides, generatedAt = generatedAt)
