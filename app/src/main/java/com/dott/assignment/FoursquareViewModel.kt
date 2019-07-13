package com.dott.assignment

import androidx.lifecycle.*
import com.dott.foursquare.RetrofitDataSource
import com.dott.foursquare.Venue
import com.dott.foursquare.VenueSearchResult
import com.dott.location.GpsLocationSource
import com.dott.location.LocationSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class FoursquareViewModel(private val gpsLocationSource: GpsLocationSource,
                          private val retrofitDataSource: RetrofitDataSource):ViewModel() {

    companion object {

        const val FOURSQUARE_CATEGORY_ID = "4d4b7105d754a06374d81259"

        const val SEARCH_RADIUS = 250

        const val LIMIT = 20
    }

    private val locationCallback = object :LocationSource.LocationCallback {
        override fun onNewLocation(latLng: LatLng) {
            viewModelScope.launch {

                @Suppress("MoveVariableDeclarationIntoWhen")
                val venueSearchResult = retrofitDataSource.searchVenues(latLng.latitude,
                    latLng.longitude,
                    FOURSQUARE_CATEGORY_ID,
                    SEARCH_RADIUS,
                    LIMIT)

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
            gpsLocationSource._locationCallback = locationCallback
        }

        override fun onInactive() {
            super.onInactive()
            gpsLocationSource._locationCallback = null
        }
    }

    val venuesLiveData:LiveData<List<Venue>>
    get() = _venuesLiveData
}