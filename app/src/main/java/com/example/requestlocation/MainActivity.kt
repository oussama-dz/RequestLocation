package com.example.requestlocation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.requestlocation.ui.LocationService
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GetCurrentLocationUI()
        }
    }
}

@Composable
fun GetCurrentLocationUI() {

    var currentLocation by remember {
        mutableStateOf("")
    }

    val scope = rememberCoroutineScope()

    val context = LocalContext.current


    val permissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (!permissions.values.all { it }) {
                //handle permission denied
            }
        }
    )


    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        Button(
            onClick = {
                scope.launch {
                    try {
                        val location = LocationService().getCurrentLocation(context)
                        currentLocation =
                            "Latitude: ${location.latitude}, Longitude: ${location.longitude}"

                    } catch (e: LocationService.LocationServiceException) {
                        when (e) {
                            is LocationService.LocationServiceException.LocationDisabledException -> {
                                //handle location disabled, show dialog or a snack-bar to enable location
                            }

                            is LocationService.LocationServiceException.MissingPermissionException -> {
                                permissionRequest.launch(
                                    arrayOf(
                                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }

                            is LocationService.LocationServiceException.NoNetworkEnabledException -> {
                                //handle no network enabled, show dialog or a snack-bar to enable network
                            }

                            is LocationService.LocationServiceException.UnknownException -> {
                                //handle unknown exception
                            }
                        }
                    }
                }
            }
        ) {
            Text(text = "Get Current Location")
        }

        Text(text = currentLocation)
    }
}
