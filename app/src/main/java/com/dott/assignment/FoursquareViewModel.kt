package com.dott.assignment

import android.location.Location
import androidx.lifecycle.*
import com.dott.foursquare.RetrofitDataSource
import com.dott.foursquare.Venue
import com.dott.foursquare.VenueSearchResult
import kotlinx.coroutines.launch

class FoursquareViewModel(private val locationHelper: LocationHelper,
                          private val retrofitDataSource: RetrofitDataSource):ViewModel() {

    companion object {

        const val FOURSQUARE_CATEGORY_ID = "4d4b7105d754a06374d81259"

        const val SEARCH_RADIUS = 250

        const val LIMIT = 20
    }

    private val observer:Observer<Location> = Observer {
        viewModelScope.launch {

            @Suppress("MoveVariableDeclarationIntoWhen")
            val venueSearchResult = retrofitDataSource.searchVenues(it.latitude,
                it.longitude,
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

    private val _venuesLiveData: MutableLiveData<List<Venue>> = object: MutableLiveData<List<Venue>>() {
        override fun onActive() {
            super.onActive()
            locationHelper.currentLocationLiveData.observeForever(observer)
        }

        override fun onInactive() {
            super.onInactive()
            locationHelper.currentLocationLiveData.removeObserver(observer)
        }
    }

    val venuesLiveData:LiveData<List<Venue>>
    get() = _venuesLiveData
}