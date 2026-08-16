package pt.socialfood.data.api

import pt.socialfood.domain.model.VisitStatus

internal val VisitStatus.pathSegment: String
    get() = when (this) {
        VisitStatus.WISH -> "wishlist"
        VisitStatus.VISITED -> "visited"
    }
