package com.assignment.location

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapCenterChangeComputerTest {

    @Test
    fun shouldNotReportSignificantChange() {
        val mapCenterChangeComputer = MapCenterChangeComputer()
        mapCenterChangeComputer.isCenterChangeSignificant(LatLng(52.3186184, 4.9428585))

        assertThat(

            //Change less than 250m
            mapCenterChangeComputer.isCenterChangeSignificant(
                LatLng(
                    52.3186184,
                    4.9428566
                )
            )
        ).isFalse()
    }

    @Test
    fun shouldReportSignificantChange() {
        val mapCenterChangeComputer = MapCenterChangeComputer()
        mapCenterChangeComputer.isCenterChangeSignificant(LatLng(52.3186184, 4.9428585))

        assertThat(

            //Change less than 250m
            mapCenterChangeComputer.isCenterChangeSignificant(
                LatLng(
                    50.320276137472554,
                    2.931683167815208
                )
            )
        ).isTrue()
    }
}