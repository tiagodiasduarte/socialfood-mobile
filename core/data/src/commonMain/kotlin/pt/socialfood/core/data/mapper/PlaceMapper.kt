package pt.socialfood.core.data.mapper

import pt.socialfood.core.model.Place
import pt.socialfood.core.network.model.place.PlaceResponse

fun PlaceResponse.toPlaces(): List<Place> = this.results.map {
    Place(
        id = it.id,
        name = it.name,
        address = it.address,
    )
}
