package com.fstech.myItems.presentation.found

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.fstech.myItems.BuildConfig.MAPS_API_KEY
import com.fstech.myItems.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.delay

@Composable
fun LocationOfLostItem(
    navigateToEnterDataOfFoundItemScreen: () -> Unit,
    viewModel: FoundItemViewModel
) {
    var isMapLoading by remember { mutableStateOf(true) }
    val locationName = remember { mutableStateOf("") }
    val locationTitle = remember { mutableStateOf("") }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        delay(1000) // Simulate 1-second delay
        isMapLoading = false
    }
//    val mapView = rememberMapViewWithLifecycle()
    val intentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        when (it.resultCode) {
            Activity.RESULT_OK -> {
                it.data?.let {
                    val place = Autocomplete.getPlaceFromIntent(it)
                    val lat = place.latLng?.latitude ?: 0.0
                    val lng = place.latLng?.longitude ?: 0.0
                    locationTitle.value = place.name?.toString() ?: ""
                    viewModel.latLng.value =
                        LatLng(place.latLng?.latitude ?: 0.0, place.latLng?.longitude ?: 0.0)
                }
            }

            AutocompleteActivity.RESULT_ERROR -> {
                it.data?.let {
                    val status = Autocomplete.getStatusFromIntent(it)
                    Log.e("MAP_ACTIVITY", status.statusMessage ?: "")
                }
            }

            Activity.RESULT_CANCELED -> {
                // The user canceled the operation.
            }
        }
    }
    val launchMapInputOverlay = {
        Places.initialize(context, MAPS_API_KEY)
        val fields = listOf(Place.Field.LAT_LNG, Place.Field.NAME)
        val intent = Autocomplete
            .IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(context)
        intentLauncher.launch(intent)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = stringResource(R.string.tell_us_where_did_you_find_this_item))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 3.dp)
                .clickable {
                    launchMapInputOverlay.invoke()
                },
            horizontalArrangement = Arrangement.Center
        ) {
            // on below line we are creating a
            // text field for our message number.
            TextField(
                // on below line we are specifying value
                // for our message text field.
                value =
                "",
//                locationName.value,
                // on below line we are adding on
                // value change for text field.
                onValueChange = {
                    launchMapInputOverlay.invoke()
//                    locationName.value = it
                },
                // on below line we are adding place holder
                // as text as "Enter your email"
                placeholder = {
                    Text(
                        text = stringResource(R.string.enter_your_location_to_search),
                        modifier = Modifier.clickable {
                            launchMapInputOverlay.invoke()
                        })
                },
                // on below line we are adding modifier to it
                // and adding padding to it and filling max width
                modifier = Modifier
                    .padding(3.dp)
                    .width(300.dp)
                    .height(60.dp)
                    .clickable {
                        launchMapInputOverlay.invoke()
                    },
                // on below line we are adding text style
                // specifying color and font size to it.
                textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
                // on below line we are adding single line to it.
                singleLine = true,
            )
            Spacer(modifier = Modifier
                .width(5.dp)
                .clickable {
                    launchMapInputOverlay.invoke()
                })

            // on below line adding a button.
            Button(
                onClick = {
                    launchMapInputOverlay.invoke()
                },
                // on below line adding a modifier for our button.
                modifier = Modifier.padding(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .height(40.dp)
                        .width(40.dp),
                    contentDescription = stringResource(R.string.search_icon),
                )
            }
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1F, false)) {
            if (isMapLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onMapClick = {
                        viewModel.latLng.value = LatLng(it.latitude, it.longitude)
                        locationName.value = ""
                        locationTitle.value = "Custom Location"
                    }, onMapLoaded = { viewModel.latLng.value = null }
                ) {
                    if (viewModel.latLng.value != null) {
                        Marker(
                            state = MarkerState(position = viewModel.latLng.value!!),
                            title = locationTitle.value.toString(),
                            snippet = locationName.value.toString(),
                        )
                    }
                }
            }
        }

        if (viewModel.latLng.value != null) {

            Button(
                onClick = navigateToEnterDataOfFoundItemScreen,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(64.dp, 8.dp)
                    .wrapContentHeight()
                    .zIndex(20F)
                    .fillMaxWidth()
            ) {
                Text(text = "Next")
            }
        }
    }
}