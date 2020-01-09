package com.assignment.ui.restaurantsmap.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.assignment.foursquare.FoursquareDataSource
import com.assignment.ui.restaurantsmap.SingleLiveEvent
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class RestaurantsViewModel(
    private val foursquareDataSource: FoursquareDataSource
) : ViewModel() {

    companion object {

        private const val SEARCH_RADIUS = 250

        private const val LIMIT = 20
    }

    val restaurantsLiveData: LiveData<FoursquareDataSource.VenueSearchResult>
        get() = _restaurantsLiveData

    private var currentLocationLiveData: LiveData<LatLng>? = null

    fun setCurrentLocationLiveData(locationLiveData: LiveData<LatLng>) {
        currentLocationLiveData = locationLiveData
    }

    private val currentLocationObserver = Observer<LatLng> {
        Log.i("RestaurantsViewModel", "Received new location:$it")
        fetchRestaurantsNear(it)
    }

    private val _restaurantsLiveData: MutableLiveData<FoursquareDataSource.VenueSearchResult> =
        object : SingleLiveEvent<FoursquareDataSource.VenueSearchResult>() {
            override fun onActive() {
                super.onActive()
                currentLocationLiveData?.observeForever(currentLocationObserver)
            }

            override fun onInactive() {
                super.onInactive()
                currentLocationLiveData?.removeObserver(currentLocationObserver)
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