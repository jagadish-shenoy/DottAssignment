package com.assignment.ui.restaurantsmap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment.foursquare.FoursquareDataSource
import com.assignment.util.SingleLiveEvent
import kotlinx.coroutines.launch

class RestaurantDetailsViewModel(private val foursquareDataSource: FoursquareDataSource) :
    ViewModel() {

    val restaurantDetailsLiveData: LiveData<FoursquareDataSource.VenueDetailsResult>
        get() = _restaurantDetailsLiveData

    fun fetchRestaurantDetails(venueId: String) {
        viewModelScope.launch {
            @Suppress("MoveVariableDeclarationIntoWhen")
            val venueDetailsResult = foursquareDataSource.fetchRestaurantDetails(venueId)
            _restaurantDetailsLiveData.postValue(venueDetailsResult)
        }
    }

    private val _restaurantDetailsLiveData =
        SingleLiveEvent<FoursquareDataSource.VenueDetailsResult>()
}