package com.assignment.ui.restaurantsmap

import androidx.lifecycle.*
import com.assignment.foursquare.FoursquareDataSource.VenueDetailsResult
import com.assignment.foursquare.FoursquareDataSource.VenueSearchResult
import com.assignment.location.LocationSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

/**
 * View model for the [RestaurantsMapFragment]
 *
 * The view models listens to new locations provided by [LocationSource]s registered with it.
 * To register a new location source, use the [addLocationSource] API
 *
 * Every time a new locations is available, the nearby restaurants are fetched using the
 * Foursquare API. Observer [restaurantsLiveData] for nearby venues.
 *
 * For more details about a restaurant, invoke [fetchRestaurantDetails] api with the venue id.
 * Observe [restaurantDetailsLiveData] for restaurant details.
 */
class FoursquareViewModel(private val foursquareDataSource: com.assignment.foursquare.FoursquareDataSource):ViewModel() {

    val restaurantsLiveData:LiveData<VenueSearchResult>
        get() = _restaurantsLiveData

    val restaurantDetailsLiveData:LiveData<VenueDetailsResult>
        get() = _restaurantDetailsLiveData

    companion object {

        const val SEARCH_RADIUS = 250

        const val LIMIT = 20
    }

    fun addLocationSource(locationSource: LocationSource) {
        locationSources.add(locationSource)
    }

    fun fetchRestaurantDetails(venueId:String) {
        viewModelScope.launch {
            @Suppress("MoveVariableDeclarationIntoWhen")
            val venueDetailsResult = foursquareDataSource.fetchRestaurantDetails(venueId)
            _restaurantDetailsLiveData.postValue(venueDetailsResult)
        }
    }

    //End of public interface

    private val locationSources = mutableSetOf<LocationSource>()

    private val _restaurantDetailsLiveData = SingleLiveEvent<VenueDetailsResult>()

    private val _restaurantsLiveData: MutableLiveData<VenueSearchResult> = object: MutableLiveData<VenueSearchResult>() {
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

    private val locationCallback = object :LocationSource.LocationCallback {
        override fun onNewLocation(latLng: LatLng) {
            viewModelScope.launch {
                val venueSearchResult = foursquareDataSource.searchRestaurants(latLng.latitude,
                    latLng.longitude,
                    SEARCH_RADIUS,
                    LIMIT
                )
                _restaurantsLiveData.postValue(venueSearchResult)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationSources.clear()
    }
}