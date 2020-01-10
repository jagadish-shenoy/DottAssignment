package com.assignment.location

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class OneShotLocationProviderTest {

    @get:Rule
    var mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var oneShotLocationProvider: OneShotLocationProvider

    @Before
    fun beforeTest() {
        oneShotLocationProvider = OneShotLocationProvider(fusedLocationProviderClient)
    }

    @Test
    fun `listen for update from location provider on active`() {
        oneShotLocationProvider.locationLiveData.observeForever {}
        verify(fusedLocationProviderClient).requestLocationUpdates(anyOrNull(), anyOrNull(), anyOrNull())
    }

    @Test
    fun `stop listen for location update on inactive`() {
        val observer = Observer<LatLng> {}
        oneShotLocationProvider.locationLiveData.observeForever(observer)
        oneShotLocationProvider.locationLiveData.removeObserver(observer)
        verify(fusedLocationProviderClient).removeLocationUpdates(any<LocationCallback>())
    }

    @Test
    fun `should push new location via location Livedata  on location update`() {
        @Suppress("UNCHECKED_CAST")
        val observer = mock(Observer::class.java) as Observer<LatLng>

        oneShotLocationProvider.locationLiveData.observeForever(observer)

        `mock location update`()

        verify(observer).onChanged(LatLng(1.0, 2.0))
    }

    @Test
    fun `should not crash if location request throws Security exception`() {
        whenever(
            fusedLocationProviderClient.requestLocationUpdates(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenThrow(SecurityException())
        oneShotLocationProvider.locationLiveData.observeForever {}
    }

    @Test
    fun `should not listen for location update after first result`() {
        oneShotLocationProvider.locationLiveData.observeForever {}
        `mock location update`()
        verify(fusedLocationProviderClient).removeLocationUpdates(any<LocationCallback>())
    }

    private fun `mock location update`() {
        val captor: ArgumentCaptor<LocationCallback> =
            ArgumentCaptor.forClass(LocationCallback::class.java)
        verify(fusedLocationProviderClient).requestLocationUpdates(
            anyOrNull(),
            captor.capture(),
            anyOrNull()
        )

        captor.value.onLocationResult(getMockLocationResult())
    }

    private fun getMockLocationResult():LocationResult {
        val location = mock(Location::class.java)

        whenever(location.latitude).thenReturn(1.0)
        whenever(location.longitude).thenReturn(2.0)
        return  LocationResult.create(listOf(location))
    }
}