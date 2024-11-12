package com.ruicomp.gpsalarm.feature.maps

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.ruicomp.gpsalarm.BuildConfig
import com.ruicomp.gpsalarm.data.fake.SearchedPlacesFakeRepo
import com.ruicomp.gpsalarm.model.PlaceAutoComplete
import com.ruicomp.gpsalarm.navigation.NavRoutes
import com.ruicomp.gpsalarm.utils.dlog
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    @ApplicationContext appContext: Context,
) : ViewModel() {

    private val _mapUiState = MutableStateFlow(MapUiState())
    val mapUiState: StateFlow<MapUiState> = _mapUiState

    private var placesClient: PlacesClient
    private var fusedLocationClient: FusedLocationProviderClient
    private var locationManager: LocationManager
    private var geocoder: Geocoder

    init {
        val (lat, lng, radius) = savedStateHandle.toRoute<NavRoutes.Maps>()
        _mapUiState.value = _mapUiState.value.copy(
            defaultCamPos = LatLng(lat!!, lng!!),
            selectedLatLng = LatLng(lat, lng),
            radius = radius.toInt(),
            zoom = 15f,
        )


        Places.initialize(appContext, BuildConfig.mapk)
        placesClient = Places.createClient(appContext)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)
        locationManager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        geocoder = Geocoder(appContext, Locale.getDefault())

        getLastKnownLocation()
        viewModelScope.launch {
            repeat(10) {
                delay(1000)
                _mapUiState.value = _mapUiState.value.copy(
                    currentLocation = LatLng(
                        (21.028238 + 0.1*it),
                        (105.234535 + 0.1*it)
                    )
                )
                dlog("current location: ${_mapUiState.value.currentLocation}")
            }
        }
//        listenLocationChange()
    }

    fun onMapClicked(latLng: LatLng) {
        Log.d("dddd", "onMapClicked: pos=($latLng)")
        _mapUiState.value = _mapUiState.value.copy(
            selectedLatLng = latLng,
            isMarkerVisible = true
        )
        viewModelScope.launch(Dispatchers.IO) {
            val address = getAddressFromLocation(
                latitude = latLng.latitude,
                longitude = latLng.longitude,
            )

            _mapUiState.value = _mapUiState.value.copy(
                selectedAddressLine = address
            )

            dlog("onMapClicked: address: $address")
            dlog("onMapClicked: thread: ${Thread.currentThread().name}")
        }
    }

    fun onRadiusChanged(radius: Int) {
        _mapUiState.value = _mapUiState.value.copy(
            radius = radius
        )
    }

    fun onBoundChange(latLngBounds: LatLngBounds?) {
        Log.d("dddd", "onMapClicked: pos=($latLngBounds)")
    }

    fun onCameraPositionChanged(latLng: LatLng, zoom: Float) {
        Log.d("dddd", "onCameraPositionChanged: pos=($latLng) zoom=($zoom)")
    }

    fun onClickSave() {
        Log.d("dddd", "onClickSave: ")
    }

    fun onFocusMyLocation() {
        _mapUiState.value = _mapUiState.value.copy(
            defaultCamPos = _mapUiState.value.currentLocation!!,
            zoom = 15f
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation()  {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                _mapUiState.value = _mapUiState.value.copy(
                    currentLocation = LatLng(
                        location.latitude,
                        location.longitude
                    )
                )
            }.addOnFailureListener {

            }
    }

    fun listenLocationChange() {
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000L,
                20f,
            ) { location ->
                _mapUiState.value = _mapUiState.value.copy(
                    currentLocation = LatLng(
                        location.latitude,
                        location.longitude
                    )
                )
            }

        } catch (ex: SecurityException) {
            // Handle permission denied
            dlog("listenLocationChange SecurityException")
        } catch (ex: Exception) {
            // Handle other exceptions
            dlog("listenLocationChange Exception")
        }
    }

    fun getAddressFromLocation(latitude: Double, longitude: Double): String? {
        return try {
            // Perform geocoding on the background thread to avoid blocking the UI
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.firstOrNull()?.let {
                dlog("getAddressFromLocation: addressLine: ${it.getAddressLine(0)}")
                dlog("getAddressFromLocation: subThoroughfare: ${it.subThoroughfare}")
                dlog("getAddressFromLocation: thoroughfare: ${it.thoroughfare}")
                dlog("getAddressFromLocation: subAdminArea: ${it.subAdminArea}")
                dlog("getAddressFromLocation: adminArea: ${it.adminArea}")
                dlog("getAddressFromLocation: countryName: ${it.countryName}")
                dlog("getAddressFromLocation: subLocality: ${it.subLocality}")
                dlog("getAddressFromLocation: locality: ${it.locality}")
            }
            // Check if addresses are found and return the first one
            addresses?.firstOrNull()?.getAddressLine(0)

        } catch (e: Exception) {
            // Log specific exceptions or handle them accordingly
            e.printStackTrace()
            null
        }
    }

    private var job: Job? = null

    fun onSearchPlaces(query: String) {
        job?.cancel()
        job = viewModelScope.launch {
            delay(500L)
            _mapUiState.value =
                _mapUiState.value.copy(listPlaces = SearchedPlacesFakeRepo.getSearchedPlaces())
            Log.d("dddd", "onSearchPlaces: ${mapUiState.value.listPlaces!!.size}")
        }
    }

    //get getCoordinates
    fun onSelectedPlace(place: PlaceAutoComplete) {
        viewModelScope.launch {
            delay(200)
            _mapUiState.value = _mapUiState.value.copy(
                defaultCamPos = LatLng(20.981836220438826, 105.86580377072096),
                selectedLatLng = LatLng(20.981836220438826, 105.86580377072096),
                zoom = 15f,
            )
        }
    }

    fun onSearchPlacesReal(query: String) {
        job?.cancel()
        job = viewModelScope.launch {
            val request = FindAutocompletePredictionsRequest
                .builder()
                .setQuery(query)
                .build()

            placesClient
                .findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    _mapUiState.value =
                        _mapUiState.value.copy(listPlaces = response.autocompletePredictions.map {
                            PlaceAutoComplete(
                                it.getPrimaryText(null).toString(),
                                it.getSecondaryText(null).toString(),
                                it.placeId,
                            )
                        })
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    println(it.cause)
                    println(it.message)
                }
        }
    }

    //get getCoordinates
    fun onSelectedPlaceReal(place: PlaceAutoComplete) {
        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(place.placeId, placeFields)
        placesClient.fetchPlace(request)
            .addOnSuccessListener {
                if (it != null) {
                    _mapUiState.value.copy(defaultCamPos = it.place.location!!)
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }


    data class MapUiState(
        val currentLocation: LatLng? = null,
        val listPlaces: List<PlaceAutoComplete>? = emptyList<PlaceAutoComplete>(),
        val selectedLatLng: LatLng? = null,
        val selectedAddressLine: String? = null,
        val defaultCamPos: LatLng = LatLng(15.62533352908947, 104.02983404695988),
        val zoom: Float = 5.8f,
        val defaultLatLngBounds: LatLngBounds = LatLngBounds(
            LatLng(
                10.764730458275112,
                107.79481396079063
            ), LatLng(13.461209117192677, 109.1738935187459)
        ),
        val isMarkerVisible: Boolean = false,
        val radius: Int = 100 // Default radius meters
    )

}