package pt.socialfood.mapper

import pt.socialfood.data.network.model.place.PlaceResponse
import pt.socialfood.domain.model.Place


fun PlaceResponse.toPlaces(): List<Place> =
    this.results.map {
        Place(
            id = it.id,
            name = it.name,
            address = it.address
        )
    }
