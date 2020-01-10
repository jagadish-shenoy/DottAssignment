package com.assignment.location

import android.location.Location
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
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class LocationSourceTest {

    @get:Rule
    var mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var oneShotLocationProvider: OneShotLocationProvider

    @Before
    fun beforeTest() {
        oneShotLocationProvider = OneShotLocationProvider(fusedLocationProviderClient)
    }

    @Test
    fun `listen for update from location provider on start`() {
        oneShotLocationProvider.start(object : OneShotLocationProvider.LocationCallback {
            override fun onNewLocation(latLng: LatLng) {}
        })
        verify(fusedLocationProviderClient).requestLocationUpdates(anyOrNull(), anyOrNull(), anyOrNull())
    }

    @Test
    fun `stop listen for update on stop`() {
        oneShotLocationProvider.stop()
        verify(fusedLocationProviderClient).removeLocationUpdates(ArgumentMatchers.any<LocationCallback>())
    }

    @Test
    fun `should invoke onNewLocation callback on location update`() {

        val callbackToTest = mock(OneShotLocationProvider.LocationCallback::class.java)
        oneShotLocationProvider.start(callbackToTest)

        `mock location update`()

        verify(callbackToTest).onNewLocation(anyOrNull())
    }

    @Test
    fun `should invoke onNewLocation callback on location update with lat long`() {

        val callbackToTest = mock(OneShotLocationProvider.LocationCallback::class.java)
        oneShotLocationProvider.start(callbackToTest)

        `mock location update`()

        verify(callbackToTest).onNewLocation(LatLng(1.0, 2.0))
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
        val callbackToTest = mock(OneShotLocationProvider.LocationCallback::class.java)
        oneShotLocationProvider.start(callbackToTest)
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