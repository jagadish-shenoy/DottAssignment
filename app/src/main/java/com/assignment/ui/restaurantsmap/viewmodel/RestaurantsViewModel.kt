package com.assignment.ui.restaurantsmap.viewmodel

import androidx.lifecycle.*
import com.assignment.foursquare.FoursquareDataSource
import com.assignment.location.LocationChangeComputer
import com.assignment.location.LocationProvider
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class RestaurantsViewModel(
    private val foursquareDataSource: FoursquareDataSource,
    private val locationProvider: LocationProvider,
    private val locationChangeComputer: LocationChangeComputer
) : ViewModel() {

    companion object {

        private const val SEARCH_RADIUS = 500

        private const val LIMIT = 20

        const val THRESHOLD_LOCATION_CHANGE = 250
    }

    val restaurantsLiveData: LiveData<FoursquareDataSource.VenueSearchResult>
        get() = _restaurantsLiveData

    private val currentLocationObserver = Observer<LatLng> {
        if (locationChangeComputer.isChangeSignificant(it)) {
            fetchRestaurantsNear(it)
        }
    }

    private val _restaurantsLiveData: MutableLiveData<FoursquareDataSource.VenueSearchResult> =
        object : MutableLiveData<FoursquareDataSource.VenueSearchResult>() {
            override fun onActive() {
                super.onActive()
                locationProvider.locationLiveData.observeForever(currentLocationObserver)
            }

            override fun onInactive() {
                super.onInactive()
                locationProvider.locationLiveData.removeObserver(currentLocationObserver)
            }
        }

    private fun fetchRestaurantsNear(latLng: LatLng) {
        viewModelScope.launch {
            val venueSearchResult = foursquareDataSource.searchRestaurants(
                latLng.latitude,
                latLng.longitude,
                SEARCH_RADIUS,
                LIMIT
            )
            _restaurantsLiveData.postValue(venueSearchResult)
        }
    }
}