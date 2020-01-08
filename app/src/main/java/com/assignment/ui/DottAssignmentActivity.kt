package com.assignment.ui

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.assignment.location.LocationPermissionHelper
import com.assignment.ui.restaurantsmap.RestaurantsMapFragment
import org.koin.android.ext.android.inject

class DottAssignmentActivity : AppCompatActivity() {

    private val locationPermissionHelper:LocationPermissionHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dottassignment)

        if(savedInstanceState == null) {
            if(locationPermissionHelper.isPermissionGranted()) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, RestaurantsMapFragment())
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, LocationPermissionRequestFragment())
                    .commit()
            }
        }
    }
}
