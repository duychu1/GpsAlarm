package com.ruicomp.gpsalarm.data.fake

import com.ruicomp.gpsalarm.model.PlaceAutoComplete

object SearchedPlacesFakeRepo {
    fun getSearchedPlaces(): List<PlaceAutoComplete> = listOf(
        PlaceAutoComplete(
            title = "Eiffel Tower",
            detailAddress = "Champ de Mars, 5 Avenue Anatole France, 75007 Paris, France",
            placeId = "ChIJd7z9F_J2mEcR2gJ0I3nTZ9Y"
        ),
        PlaceAutoComplete(
            title = "Statue of Liberty",
            detailAddress = "Liberty Island, New York, NY 10004, United States",
            placeId = "ChIJL4Zg7ShawokRna7Gjs_OmS8"
        ),
        PlaceAutoComplete(
            title = "Great Wall of China",
            detailAddress = "Huairou District, Beijing, China",
            placeId = "ChIJ9xF5cS9K8jAR_rkKzgOfcEc"
        ),
        PlaceAutoComplete(
            title = "Colosseum",
            detailAddress = "Piazza del Colosseo, 1, 00184 Roma RM, Italy",
            placeId = "ChIJkWcR0UNzLxMRUAC4R7SeZ8E"
        ),
        PlaceAutoComplete(
            title = "Machu Picchu",
            detailAddress = "Machu Picchu, 08680, Peru",
            placeId = "ChIJt7wiD77U8pMR5fD1SnRMDXo"
        )
    )

}