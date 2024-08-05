package com.fstech.myItems.presentation.lost

import android.location.Geocoder
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.fstech.myItems.R
import com.fstech.myItems.presentation.found.circularProgressIndicator
import com.jetawy.domain.models.ItemLost
import com.jetawy.domain.utils.UiState
import java.util.Locale

@Composable
fun LostItemEnterDataScreen(function: () -> Unit, viewModel: LostItemViewModel) {
    val context = LocalContext.current
    LaunchedEffect("startTheMainTaskUploadLost") {
        viewModel.resetStates()
        val itemLost = ItemLost(
            type = viewModel.type.value,
            model = viewModel.model.value,
            brand = viewModel.brand.value,
            category = viewModel.category.value,
            itemState = viewModel.itemState.value,
            colors = viewModel.colors,
            imageDescription = viewModel.imageDescription.value,
            userDescription = viewModel.userDescription.value
        )
        //I want to add photos to the itemLost object to make ai describe it better
        //generate category
        viewModel.translatePrompt(
            itemLost,
            "\ncan you translate the data in this model to English leave parameters as it is and return it as json format"
        )
    }
    Column {

        when (viewModel.uiState.collectAsState().value) {
            is UiState.Error -> {
                Toast.makeText(
                    context,
                    (viewModel.uiState.value as UiState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetStates()
            }

            UiState.Initial -> {

            }

            UiState.Loading -> {
                Text(text = stringResource(R.string.checking_entries))
                CircularProgressIndicator()
            }

            is UiState.Success<*> -> {
                LaunchedEffect("upload") {
                    val itemLost: ItemLost = ItemLost(
                        viewModel.type.value,
                        viewModel.model.value,
                        viewModel.brand.value,
                        viewModel.category.value,
                        viewModel.itemState.value,
                        viewModel.colors,
                        viewModel.imageDescription.value,
                        viewModel.userDescription.value,
                        viewModel.translatedDescription.value
                    )
                    val lat = viewModel.latLng.value?.latitude ?: 0.0
                    val lng = viewModel.latLng.value?.longitude ?: 0.0
                    val geocoder = Geocoder(context, Locale.ENGLISH)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(lat, lng, 1)
                        { addresses ->
                            viewModel.addresses = addresses
                            viewModel.uploadItems(
                                imageUris = viewModel.list,
                                addresses = viewModel.addresses!![0],
                                aiResponse = viewModel.aiResponse,
                                userResponse = itemLost,
                                userDescription = viewModel.userDescription.value ?: "",
//                        description = viewModel.userDescription.value?:""
                            )
                        }
                    } else {
                        val addresses = geocoder.getFromLocation(lat, lng, 1)
                        viewModel.addresses = addresses
                        viewModel.uploadItems(
                            imageUris = viewModel.list,
                            addresses = viewModel.addresses!![0],
                            aiResponse = itemLost,
                            userDescription = viewModel.userDescription.value ?: "",
                            userResponse = itemLost
                        )
                    }
                }

                when (viewModel.uploadItems.collectAsState().value) {
                    is UiState.Error -> {
                        Toast.makeText(
                            context,
                            (viewModel.uploadItems.value as UiState.Error).message,
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.resetUploadItems()
                    }

                    UiState.Initial -> {}
                    UiState.Loading -> {
                        Text(text = stringResource(R.string.uploading_your_item))
                        circularProgressIndicator()
                    }

                    is UiState.Success<*> -> {
                        function()
                    }
                }
            }
        }

    }
}


