package com.assignment.ui.restaurantsmap

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.assignment.foursquare.FoursquareDataSource
import com.assignment.foursquare.FoursquareDataSource.VenueSearchResult
import com.assignment.foursquare.Venue
import com.assignment.foursquare.VenueDetails
import com.assignment.location.LocationProvider
import com.assignment.ui.restaurantsmap.viewmodel.RestaurantDetailsViewModel
import com.assignment.ui.restaurantsmap.viewmodel.RestaurantsViewModel
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule


class RestaurantsMapFragmentTest {

    @get:Rule
    var mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var restaurantsViewModel: RestaurantsViewModel

    @Mock
    private lateinit var restaurantDetailsViewModel: RestaurantDetailsViewModel

    @Mock
    private lateinit var locationProvider: LocationProvider



    private val testVenue = Venue(id="47d14cb744d8cfa8c5fbc37",
        name="Test Cafe",
        latitude=52.31492479404724,
        longitude=4.935527745830467)

    private val testVenueDetails = VenueDetails(
        "1",
        "Venue Name",
        "Venue Description",
        "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png",
        "Venue Address",
        "+31123456789",
        "6.0"
    )

    @Before
    fun beforeTest() {
        setupMockLiveData()

        loadKoinModules(
            module(override = true) {
                viewModel { restaurantsViewModel }
                viewModel { restaurantDetailsViewModel }
                single { locationProvider }
            }
        )
    }

    @Test
    fun verifyMapMarkerIsShownForMenu() {
        launchFragmentInContainer<RestaurantsMapFragment>()

        (restaurantsViewModel.restaurantsLiveData as MutableLiveData<VenueSearchResult>).postValue(
            VenueSearchResult.Success(52.31492479404724, 4.935527745830467, venues = listOf(testVenue)))

        val device = UiDevice.getInstance(getInstrumentation())
        val marker = device.findObject(UiSelector().descriptionContains("Test Cafe"))
        marker.waitForExists(2000)
        marker.click()
    }

    private fun setupMockLiveData() {
        val restaurantsLiveData = MutableLiveData<VenueSearchResult>()
        val restaurantDetailsLiveData = MutableLiveData<FoursquareDataSource.VenueDetailsResult>()
        val locationLiveData = MutableLiveData<LatLng>()

        whenever(restaurantsViewModel.restaurantsLiveData).thenReturn(restaurantsLiveData)
        whenever(restaurantDetailsViewModel.restaurantDetailsLiveData).thenReturn(restaurantDetailsLiveData)
        whenever(locationProvider.locationLiveData).thenReturn(locationLiveData)
    }
}