package com.assignment.ui.restaurantsmap.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.aniketkadam.sharevideoshortcut.CoroutineTestRule
import com.assignment.foursquare.FoursquareDataSource
import com.assignment.foursquare.FoursquareDataSource.VenueSearchResult
import com.assignment.foursquare.Venue
import com.assignment.location.LocationChangeComputer
import com.assignment.location.LocationProvider
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyDouble
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class RestaurantsViewModelTest {

    @get:Rule
    var mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Mock
    private lateinit var foursquareDataSource: FoursquareDataSource

    @Mock
    private lateinit var locationProvider: LocationProvider

    @Mock
    private lateinit var locationChangeComputer: LocationChangeComputer

    private lateinit var restaurantsViewModel: RestaurantsViewModel

    private lateinit var locationLiveData: MutableLiveData<LatLng>
    @Before
    fun beforeTest() {

        locationLiveData = MutableLiveData()
        whenever(locationProvider.locationLiveData).thenReturn(locationLiveData)

        restaurantsViewModel =
            RestaurantsViewModel(foursquareDataSource, locationProvider, locationChangeComputer)
    }

    @Test
    fun `should register for live location update on restaurant livedata active`() {
        restaurantsViewModel.restaurantsLiveData.observeForever {}
        assertThat(locationLiveData.hasActiveObservers()).isTrue()
    }

    @Test
    fun `should deregister for live location update on restaurant livedata inactive`() {

        val observer = Observer<VenueSearchResult> {}
        restaurantsViewModel.restaurantsLiveData.observeForever(observer)
        restaurantsViewModel.restaurantsLiveData.removeObserver(observer)

        assertThat(locationLiveData.hasActiveObservers()).isFalse()
    }

    @Test
    fun `should start fetching restaurants once location is available is location change is significant`() {

        locationLiveData.value = LatLng(1.0, 2.0)
        whenever(locationChangeComputer.isChangeSignificant(anyOrNull())).thenReturn(true)

        restaurantsViewModel.restaurantsLiveData.observeForever {}

        runBlocking {
            verify(foursquareDataSource).searchRestaurants(1.0, 2.0, 500, 20)
        }
    }

    @Test
    fun `should NOT start fetching restaurants once location is available is location change is NOT significant`() {

        locationLiveData.value = LatLng(1.0, 2.0)
        whenever(locationChangeComputer.isChangeSignificant(anyOrNull())).thenReturn(false)

        restaurantsViewModel.restaurantsLiveData.observeForever {}

        runBlocking {
            verify(foursquareDataSource, never()).searchRestaurants(1.0, 2.0, 500, 20)
        }
    }

    @Test
    fun `should notify ui once venue search result is available`() {
        locationLiveData.value = LatLng(1.0, 2.0)
        whenever(locationChangeComputer.isChangeSignificant(anyOrNull())).thenReturn(true)

        @Suppress("UNCHECKED_CAST")
        val venueSearchResultObserver = mock(Observer::class.java) as Observer<VenueSearchResult>
        restaurantsViewModel.restaurantsLiveData.observeForever(venueSearchResultObserver)


        runBlocking {
            whenever(
                foursquareDataSource.searchRestaurants(
                    anyDouble(),
                    anyDouble(),
                    anyInt(),
                    anyInt()
                )
            ).thenReturn(
                VenueSearchResult.Success(1.0, 2.0, venues)
            )
            verify(venueSearchResultObserver).onChanged(anyOrNull())
        }
    }

    private val venues = listOf(
        Venue("1", "Venue Name", 1.0, 2.0)
    )
}