package com.assignment.foursquare

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import retrofit2.Response

class FoursquareDataSourceTest {
    @Rule
    @JvmField
    var mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var venueService: com.assignment.foursquare.VenueService

    private lateinit var foursquareDataSource: com.assignment.foursquare.FoursquareDataSource

    @Before
    fun before() {
        foursquareDataSource = com.assignment.foursquare.FoursquareDataSource(venueService)
    }

    @Test
    fun `searchRestaurants returns VenueSearchResult Failure if response is not successful`() {
        runBlocking {
            val response = Response.error<com.assignment.foursquare.Venues>(400, ResponseBody.create(null, ""))
            whenever(venueService.searchVenues(anyOrNull(), anyOrNull(), anyInt(), anyInt())).thenReturn(response)
            assertThat(foursquareDataSource.searchRestaurants(0.0, 0.0, 100, 100))
                .isEqualTo(VenueSearchResult.Failure)
        }
    }

    @Test
    fun `searchRestaurants return VenueSearchResult Failure if response body is empty`() {
        runBlocking {
            val response = Response.success<com.assignment.foursquare.Venues>(null)
            whenever(venueService.searchVenues(anyOrNull(), anyOrNull(), anyInt(), anyInt())).thenReturn(response)
            assertThat(foursquareDataSource.searchRestaurants(0.0, 0.0, 100, 100))
                .isEqualTo(VenueSearchResult.Failure)
        }
    }

    @Test
    fun `searchRestaurants return VenueSearchResult Success if response is valid`() {
        val venue = com.assignment.foursquare.Venue("1", "Venue", 1.72, 1.72)
        val response = Response.success(com.assignment.foursquare.Venues(listOf((venue))))
        runBlocking {
            whenever(venueService.searchVenues(anyOrNull(), anyOrNull(), anyInt(), anyInt())).thenReturn(response)

            val observedResponse = foursquareDataSource.searchRestaurants(0.0, 0.0, 100, 100)
            assertThat(observedResponse is VenueSearchResult.Success).isTrue()
            assertThat((observedResponse as VenueSearchResult.Success).venues).isEqualTo(listOf(venue))
        }
    }

}