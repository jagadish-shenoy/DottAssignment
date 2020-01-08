package com.assignment.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.assignment.location.LocationPermissionHelper
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val locationPermissionHelper:LocationPermissionHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mian)
    }
}
