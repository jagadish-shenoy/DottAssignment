package com.dott.assignment.restaurantsmap

import androidx.lifecycle.*
import com.dott.foursquare.*
import com.dott.location.LocationSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class FoursquareViewModel(private val foursquareDataSource: FoursquareDataSource):ViewModel() {

    companion object {

        const val SEARCH_RADIUS = 250

        const val LIMIT = 20
    }

    private val locationSources = mutableSetOf<LocationSource>()

    private val locationCallback = object :LocationSource.LocationCallback {
        override fun onNewLocation(latLng: LatLng) {
            viewModelScope.launch {

                @Suppress("MoveVariableDeclarationIntoWhen")
                val venueSearchResult = foursquareDataSource.searchRestaurants(latLng.latitude,
                    latLng.longitude,
                    SEARCH_RADIUS,
                    LIMIT
                )

                when(venueSearchResult) {
                    is VenueSearchResult.Success -> {
                        _venuesLiveData.postValue(venueSearchResult.venues)
                    }
                    is VenueSearchResult.Failure -> {}
                }
            }
        }
    }

    private val _venuesLiveData: MutableLiveData<List<Venue>> = object: MutableLiveData<List<Venue>>() {
        override fun onActive() {
            super.onActive()
            locationSources.forEach { it.locationCallback = locationCallback }
        }

        override fun onInactive() {
            super.onInactive()
            locationSources.forEach { it.locationCallback = null }
        }
    }

    val venuesLiveData:LiveData<List<Venue>>
    get() = _venuesLiveData

    private val _venueDetailsLiveData = SingleLiveEvent<VenueDetails>()

    val venueDetailsLiveData:LiveData<VenueDetails>
    get() = _venueDetailsLiveData

    fun addLocationSource(locationSource: LocationSource) {
        locationSources.add(locationSource)
    }

    fun fetchVenueDetails(venueId:String) {
        viewModelScope.launch {
            @Suppress("MoveVariableDeclarationIntoWhen")
            val venueDetailsResult = foursquareDataSource.fetchRestaurantDetails(venueId)
            when(venueDetailsResult) {
                is VenueDetailsResult.Success -> _venueDetailsLiveData.postValue(venueDetailsResult.venueDetails)
                is VenueDetailsResult.Failure -> {}
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationSources.clear()
    }
}