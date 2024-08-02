package com.fstech.myItems.presentation.found

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstech.myItems.R
import com.jetawy.domain.models.ItemFound
import com.jetawy.domain.utils.UiState
import java.util.Locale


@Composable
fun FoundItemEnterDataScreen(
    goToFountItemSuccessScreen: () -> Unit,
    viewModel: FoundItemViewModel
) {
    val context = LocalContext.current
    val uploadingItems = viewModel.uploadItems.collectAsState()
    val aiResponse =
        (viewModel.uiState.collectAsState().value as UiState.Success<*>).outputData as ItemFound
    Column {
        StringInputTextField(
            value = viewModel.userDescription.value?:"",
            onValueChange = { viewModel.userDescription.value = it },
            label = stringResource(R.string.can_you_tell_us_more_details_or_description_of_the_item_to_help_us_matching_it_to_it_s_owner_faster)
        )
        when (uploadingItems.value) {
            is UiState.Error -> {
                Toast.makeText(
                    context,
                    (uploadingItems.value as UiState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetUploadItems()
            }

            UiState.Initial -> {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    onClick = {
                        uploadDataRoutine(context, viewModel, aiResponse, viewModel.userDescription.value?:"")
                    }) { Text(text = stringResource(R.string.upload_data)) }
            }

            UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(64.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

            is UiState.Success<*> -> {
                goToFountItemSuccessScreen()
            }
        }
    }
}

fun uploadDataRoutine(
    context: Context,
    viewModel: FoundItemViewModel,
    aiResponse: ItemFound,
    userDescription: String
) {
    aiResponse.userDescription = userDescription
    val lat = viewModel.latLng.value?.latitude ?: 0.0
    val lng = viewModel.latLng.value?.longitude ?: 0.0
    val geocoder = Geocoder(context, Locale.ENGLISH)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        geocoder.getFromLocation(lat, lng, 1) { addresses ->
            viewModel.addresses = addresses
            viewModel.uploadItems(
                imageUris = viewModel.list,
                addresses = addresses[0],
                aiResponse,
            )
        }
    } else {
        val addresses = geocoder.getFromLocation(lat, lng, 1)
        viewModel.addresses = addresses
        viewModel.uploadItems(
            imageUris = viewModel.list,
            addresses = addresses?.get(0)!!,
            aiResponse,
        )
    }
}

@Composable
fun StringInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    showError: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        singleLine = false,
        shape = RoundedCornerShape(16.dp), // Rounded corners for a softer look
        maxLines = 6,
        minLines = 1,
        textStyle = MaterialTheme.typography.bodyMedium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),  supportingText = {
            if (showError.isNotEmpty()) {
                Text(
                    text = showError,
                    color = Color.Red
                )
            }
        },
    )
}