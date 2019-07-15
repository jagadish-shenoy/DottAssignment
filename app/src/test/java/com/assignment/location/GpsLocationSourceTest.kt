package com.assignment.location

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
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

class GpsLocationSourceTest {

    @Rule
    @JvmField
    var mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var gpsLocationSource: GpsLocationSource

    @Before
    fun beforeTest() {
        gpsLocationSource = GpsLocationSource(fusedLocationProviderClient)
    }

    @Test
    fun `listen for update from location provider onActive`() {
        gpsLocationSource.locationCallback = object: LocationSource.LocationCallback {
            override fun onNewLocation(latLng: LatLng) {}
        }
        verify(fusedLocationProviderClient).requestLocationUpdates(anyOrNull(), anyOrNull(), anyOrNull())
    }

    @Test
    fun `stop listen for update on provider inActive`() {
        //First active
        gpsLocationSource.locationCallback = object: LocationSource.LocationCallback {
            override fun onNewLocation(latLng: LatLng) {}
        }
        //Then inActive
        gpsLocationSource.locationCallback = null

        verify(fusedLocationProviderClient).removeLocationUpdates(ArgumentMatchers.any<LocationCallback>())
    }

    @Test
    fun `should invoke onNewLocation callback on location update`() {

        val callbackToTest = object: LocationSource.LocationCallback {
            var isTriggered = false
            override fun onNewLocation(latLng: LatLng) {
                isTriggered = true
            }
        }
        gpsLocationSource.locationCallback = callbackToTest

        val captor:ArgumentCaptor<LocationCallback> = ArgumentCaptor.forClass(LocationCallback::class.java)
        verify(fusedLocationProviderClient).requestLocationUpdates(anyOrNull(), captor.capture(), anyOrNull())

        captor.value.onLocationResult(getMockLocationResult())

        assertThat(callbackToTest.isTriggered).isTrue()
    }

    @Test
    fun `should invoke onNewLocation callback on location update with lat long`() {

        val callbackToTest = object: LocationSource.LocationCallback {
            var latLng:LatLng? = null
            override fun onNewLocation(latLng: LatLng) {
                this.latLng = latLng
            }
        }
        gpsLocationSource.locationCallback = callbackToTest

        val captor:ArgumentCaptor<LocationCallback> = ArgumentCaptor.forClass(LocationCallback::class.java)
        verify(fusedLocationProviderClient).requestLocationUpdates(anyOrNull(), captor.capture(), anyOrNull())

        captor.value.onLocationResult(getMockLocationResult())

        assertThat(callbackToTest.latLng).isNotNull
        assertThat(callbackToTest.latLng).isEqualTo(LatLng(1.0, 2.0))
    }

    private fun getMockLocationResult():LocationResult {
        val location = mock(Location::class.java)
        whenever(location.latitude).thenReturn(1.0)
        whenever(location.longitude).thenReturn(2.0)
        return  LocationResult.create(listOf(location))
    }
}