package com.ruicomp.gpsalarm.data.fake

import com.ruicomp.gpsalarm.model.GpsLocation
import com.ruicomp.gpsalarm.model.PlaceAutoComplete

object SearchedPlacesFakeRepo {
    fun getSearchedPlaces(): List<PlaceAutoComplete> = listOf(
        // Europe
        PlaceAutoComplete(
            title = "Europe",
            detailAddress = "Europe",
            placeId = "europe",
            gpsLocation = GpsLocation(54.5260, 15.2551) // Center of Europe
        ),
        // North America
        PlaceAutoComplete(
            title = "North America",
            detailAddress = "North America",
            placeId = "north_america",
            gpsLocation = GpsLocation(46.0730, -100.5469) // Center of North America
        ),
        // Asia
        PlaceAutoComplete(
            title = "Asia",
            detailAddress = "Asia",
            placeId = "asia",
            gpsLocation = GpsLocation(34.0479, 100.6197) // Center of Asia
        ),
        // South America
        PlaceAutoComplete(
            title = "South America",
            detailAddress = "South America",
            placeId = "south_america",
            gpsLocation = GpsLocation(-14.2350, -51.9253) // Center of South America
        ),
        // Africa
        PlaceAutoComplete(
            title = "Africa",
            detailAddress = "Africa",
            placeId = "africa",
            gpsLocation = GpsLocation(7.1881, 21.0936) // Center of Africa
        )
    )

}