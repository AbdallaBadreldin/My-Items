package com.fstech.myItems.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(navController: NavController) {
    val atasehir = LatLng(40.9971, 29.1007)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(atasehir, 15f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState
    )
    Row {
        Button(
            onClick = { navController.navigate(Screen.BakingScreen.route) },
            modifier = Modifier
                .align(Alignment.Bottom)
                .padding(64.dp,24.dp,64.dp,16.dp)
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            Text("DONE")
        }
    }
}