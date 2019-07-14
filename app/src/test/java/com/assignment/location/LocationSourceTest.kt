package com.assignment.location

import com.google.android.gms.maps.model.LatLng
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LocationSourceTest {

    class MockLocationSource:LocationSource() {

        var onActiveCount = 0

        var onInActiveCount = 0

        override fun onActive() {
            onActiveCount++
        }

        override fun onInActive() {
            onInActiveCount++
        }
    }


    private val mockLocationSource = MockLocationSource()

    @Test
    fun `should invoke onActive when the first callback is registered`() {
        mockLocationSource.locationCallback = object :LocationSource.LocationCallback {
            override fun onNewLocation(latLng: LatLng) {}
        }

        assertThat(mockLocationSource.onActiveCount).isEqualTo(1)
    }

    @Test
    fun `onActive should be invoked only once`() {

        mockLocationSource.locationCallback = object :LocationSource.LocationCallback {
            override fun onNewLocation(latLng: LatLng) {}
        }

        mockLocationSource.locationCallback = object :LocationSource.LocationCallback {
            override fun onNewLocation(latLng: LatLng) {}
        }

        assertThat(mockLocationSource.onActiveCount).isEqualTo(1)
    }

    @Test
    fun `onInActive should be invoked when the callback is cleared`() {

        mockLocationSource.locationCallback = object :LocationSource.LocationCallback {
            override fun onNewLocation(latLng: LatLng) {}
        }

        mockLocationSource.locationCallback = null

        assertThat(mockLocationSource.onInActiveCount).isEqualTo(1)
    }

    @Test
    fun `onInActive should be invoked only once`() {

        mockLocationSource.locationCallback = object :LocationSource.LocationCallback {
            override fun onNewLocation(latLng: LatLng) {}
        }

        mockLocationSource.locationCallback = null
        mockLocationSource.locationCallback = null

        assertThat(mockLocationSource.onInActiveCount).isEqualTo(1)
    }
}