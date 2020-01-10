package com.assignment.ui.restaurantsmap.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.aniketkadam.sharevideoshortcut.CoroutineTestRule
import com.assignment.foursquare.FoursquareDataSource
import com.assignment.foursquare.FoursquareDataSource.VenueSearchResult
import com.assignment.foursquare.Venue
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
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

    private lateinit var restaurantsViewModel: RestaurantsViewModel

    @Before
    fun beforeTest() {
        restaurantsViewModel = RestaurantsViewModel(foursquareDataSource)
    }

    @Test
    fun `should register for live location update on restaurant livedata active`() {
        @Suppress("UNCHECKED_CAST")
        val locationLiveData = mock(LiveData::class.java) as LiveData<LatLng>
        restaurantsViewModel.setCurrentLocationLiveData(locationLiveData)

        restaurantsViewModel.restaurantsLiveData.observeForever {}

        verify(locationLiveData).observeForever(anyOrNull())
    }

    @Test
    fun `should deregister for live location update on restaurant livedata inactive`() {
        @Suppress("UNCHECKED_CAST")
        val locationLiveData = mock(LiveData::class.java) as LiveData<LatLng>
        restaurantsViewModel.setCurrentLocationLiveData(locationLiveData)

        val observer = Observer<VenueSearchResult> {}
        restaurantsViewModel.restaurantsLiveData.observeForever(observer)
        restaurantsViewModel.restaurantsLiveData.removeObserver(observer)

        verify(locationLiveData).removeObserver(anyOrNull())
    }

    @Test
    fun `should start fetching restaurants once location is available`() {
        restaurantsViewModel.setCurrentLocationLiveData(getMockLocationLiveData())

        restaurantsViewModel.restaurantsLiveData.observeForever {}

        runBlocking {
            verify(foursquareDataSource).searchRestaurants(1.0, 2.0, 250, 20)
        }
    }

    @Test
    fun `should notify ui once venue search result is available`() {
        restaurantsViewModel.setCurrentLocationLiveData(getMockLocationLiveData())

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

    private fun getMockLocationLiveData() = MutableLiveData<LatLng>().apply {
        value = LatLng(1.0, 2.0)
    }

    private val venues = listOf(
        Venue("1", "Venue Name", 1.0, 2.0)
    )
}