package com.assignment.ui.restaurantsmap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment.foursquare.FoursquareDataSource
import com.assignment.location.GpsLocationSource
import com.assignment.location.LocationSource
import com.assignment.location.MapPanLocationSource
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class RestaurantsViewModel(
    private val foursquareDataSource: FoursquareDataSource,
    private val gpsLocationSource: GpsLocationSource
) :
    ViewModel() {

    companion object {

        private const val SEARCH_RADIUS = 250

        private const val LIMIT = 20
    }

    val restaurantsLiveData: LiveData<FoursquareDataSource.VenueSearchResult>
        get() = _restaurantsLiveData

    private val locationSources = mutableSetOf<LocationSource>()


    fun onMapReady(googleMap: GoogleMap) {
        locationSources.add(gpsLocationSource)
        locationSources.add(MapPanLocationSource(googleMap))
    }

    private val _restaurantsLiveData: MutableLiveData<FoursquareDataSource.VenueSearchResult> =
        object : MutableLiveData<FoursquareDataSource.VenueSearchResult>() {
            override fun onActive() {
                super.onActive()
                //Listen for new location when the live data is observed
                locationSources.forEach { it.locationCallback = locationCallback }
            }

            override fun onInactive() {
                super.onInactive()
                locationSources.forEach { it.locationCallback = null }
            }
        }

    private val locationCallback = object : LocationSource.LocationCallback {
        override fun onNewLocation(latLng: LatLng) {
            viewModelScope.launch {
                val venueSearchResult = foursquareDataSource.searchRestaurants(
                    latLng.latitude,
                    latLng.longitude,
                    SEARCH_RADIUS,
                    LIMIT
                )
                
                if (venueSearchResult is FoursquareDataSource.VenueSearchResult.Success &&
                    venueSearchResult.venues.isNotEmpty()
                ) {
                    _restaurantsLiveData.postValue(venueSearchResult)
                } else if (venueSearchResult is FoursquareDataSource.VenueSearchResult.Failure) {
                    _restaurantsLiveData.postValue(venueSearchResult)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationSources.clear()
    }
}