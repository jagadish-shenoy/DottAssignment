package com.assignment.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.assignment.location.LocationPermissionHelper
import kotlinx.android.synthetic.main.activity_mian.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val locationPermissionHelper:LocationPermissionHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mian)

        if (savedInstanceState == null && locationPermissionHelper.isPermissionGranted()) {
            val navHostFragment = nav_host_fragment as NavHostFragment
            val inflater = navHostFragment.navController.navInflater
            val graph = inflater.inflate(R.navigation.nav_graph)
            graph.startDestination = R.id.restaurantsMapFragment
            navHostFragment.navController.graph = graph
        }
    }
}
