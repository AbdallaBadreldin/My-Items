package com.fstech.myItems.presentation.found

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fstech.myItems.BuildConfig
import com.fstech.myItems.R
import com.fstech.myItems.presentation.getAppLanguage
import com.jetawy.domain.models.ItemResponse
import com.jetawy.domain.utils.UiState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

const val maxImagesToScan = 5
const val minimumImagesToDetect = 3

@Composable
fun FoundItemScreen(
    gotoLocationOfLostItems: () -> Unit, viewModel: FoundItemViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        context,
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            if (!it || uri == null || uri == Uri.EMPTY || uri.path == null || uri.path?.isEmpty() == true || uri.path == "null" || uri.path == "content://" || uri.path == "file://" || uri.path == "android.resource://" || uri.path == "com.android.providers.media.documents")
                return@rememberLauncherForActivityResult
            else {
                viewModel.resetStates()
                if (viewModel.list.size < maxImagesToScan) {
                    viewModel.addItem(uri)
                }
            }
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(
                context,
                context.getString(R.string.permission_granted), Toast.LENGTH_SHORT
            ).show()
            if (viewModel.list.size < maxImagesToScan) {
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(
                    context,
                    "You can only take $maxImagesToScan images",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(context, context.getString(R.string.need_permission), Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun uriToBitmap(contentResolver: ContentResolver, imageUri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(imageUri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            // Handle exceptions (e.g., file not found, invalid URI)
            null
        }
    }

    fun openCameraRoutine() {
        val permissionCheckResult =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            if (viewModel.list.size < maxImagesToScan) {
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(
                    /* context = */ context,
                    /* text = */
                    context.getString(
                        R.string.you_can_only_take_images,
                        maxImagesToScan.toString()
                    ),
                    /* duration = */ Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // Request a permission
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            text = stringResource(R.string.add_at_least_three_images_for_found_item)
        )
        Button(onClick = {
            openCameraRoutine()
        }, modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(R.string.capture_image_from_camera))
        }

        if (viewModel.list.isEmpty()) {
            Image(
                painterResource(id = R.drawable.round_add_circle_outline_24),
                contentDescription = stringResource(R.string.add_image),
                modifier = Modifier
                    .padding(16.dp)
                    .width(128.dp)
                    .height(128.dp)
                    .wrapContentHeight()
                    .clickable { openCameraRoutine() },
            )
        } else {
            LazyRow(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
            ) {
                itemsIndexed(viewModel.list) { index, item ->
                    ImageOfUri(item, index, viewModel, context)
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        if (viewModel.list.size >= minimumImagesToDetect) when (uiState) {
            is UiState.Error -> {
                val errorString = (uiState as UiState.Error).message
                Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show()
                Text(text = stringResource(R.string.please_try_again))
            }

            UiState.Initial -> {
                Button(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    onClick = {
                        val contentResolver = context.contentResolver
                        val bitmapList = mutableListOf<Bitmap>()
                        viewModel.list.forEach {
                            if (it != null || it != Uri.EMPTY) {
                                uriToBitmap(contentResolver, it)?.let { it1 ->
                                    bitmapList.add(
                                        it1
                                    )
                                }
                            } else {
                                // Handle the error (e.g., display an error message)
                            }
                        }
                        viewModel.sendPrompt(
                            bitmapList,
                            context.getString(
                                R.string.can_you_return_the_name_of_the_object_in_images_as_string_in_parameter_called_name_and_name_of_the_object_in_images_as_string_in_language_in_parameter_called_namelocallanguage_and_description_as_string_of_the_main_object_in_images_in_parameter_called_description_and_color_as_list_of_the_main_object_in_images_in_parameter_called_color_and_brand_as_string_and_category_as_string_of_the_object_in_the_images_in_parameter_called_images_in_json_object_format,
                                getAppLanguage()
                            )
                        )
                    }) {
                    Text(text = stringResource(R.string.detect_object))
                }
            }

            UiState.Loading -> {
                circularProgressIndicator()
            }

            is UiState.Success<*> -> {
                val response = (uiState as UiState.Success<*>).outputData as ItemResponse
                Column {
                    Text(
                        text = stringResource(R.string.the_item_is, response.nameLocalLanguage),
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Image(
                            modifier = Modifier
                                .height(48.dp)
                                .weight(1f) // Occupy the other half of the width
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.resetStates()
                                    viewModel.list.clear()
                                },
                            painter = painterResource(id = R.drawable.close),
                            contentDescription = stringResource(R.string.the_item_is_wrong)
                        ) // Replace with your desired icon
                        Image(
                            modifier = Modifier
                                .height(48.dp)
                                .weight(1f) // Occupy the other half of the width
                                .fillMaxWidth()
                                .clickable {
                                    gotoLocationOfLostItems()
                                },
                            painter = painterResource(id = R.drawable.icon_true),
                            contentDescription = stringResource(R.string.the_item_is_correct)
                        ) // Replace with your desired icon
                    }
                }
            }
        } else viewModel.resetStates()
    }
}


fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}

@Composable
fun ImageOfUri(uri: Uri, uriId: Int, viewModel: FoundItemViewModel, context: Context) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            modifier = Modifier
                .padding(5.dp)
                .width(128.dp)
                .wrapContentHeight(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .error(R.drawable.round_add_circle_outline_24)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.captured_image),
            onError = {
                viewModel.list.removeAt(uriId)
                Toast.makeText(
                    context,
                    context.getString(R.string.error_loading_image), Toast.LENGTH_SHORT
                ).show()
            }
        )
        // Add close button on top left
        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp), // Adjust padding as needed
            onClick = {
                viewModel.list.removeAt(uriId)
                viewModel.resetStates()
            }
        ) {
            Image(
                painterResource(id = R.drawable.close),
                contentDescription = stringResource(R.string.delete_image),
            ) // Replace with your desired icon
        }
    }
}

@Composable
fun circularProgressIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.width(64.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}