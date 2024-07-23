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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fstech.myItems.BuildConfig
import com.fstech.myItems.R
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.jetawy.domain.utils.UiState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

const val maxImagesToScan = 5
const val minimumImagesToDetect = 3

@OptIn(ExperimentalCoilApi::class)
@Composable
fun FoundItemScreen(navController: NavController, viewModel: FoundItemViewModel = viewModel()) {

    var result by rememberSaveable { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            if (viewModel.list.size < maxImagesToScan) {
                if (it)
                    viewModel.addItem(uri)
            }
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            if (viewModel.list.size < maxImagesToScan)
                cameraLauncher.launch(uri)
            else {
                Toast.makeText(
                    context,
                    "You can only take $maxImagesToScan images",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
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
            if (viewModel.list.size < maxImagesToScan)
                cameraLauncher.launch(uri)
            else {
                Toast.makeText(
                    context,
                    "You can only take $maxImagesToScan images",
                    Toast.LENGTH_SHORT
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
        }) {
            Text(text = "Capture Image From Camera")
        }

        if (viewModel.list.isEmpty()) {
            Image(
                painterResource(id = R.drawable.round_add_circle_outline_24),
                contentDescription = "add Image",
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
                    .fillMaxWidth()
                    .wrapContentHeight(),
            ) {
                itemsIndexed(viewModel.list) { index, item ->
                    ImageOfUri(item, index, viewModel)
                }
            }
        }

        if (viewModel.list.size >= minimumImagesToDetect) {
            Button(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                onClick = {
                    val contentResolver = context.contentResolver
                    val bitmapList = mutableListOf<Bitmap>()
                    viewModel.list.forEach {
                        if (it != null) {
                            uriToBitmap(contentResolver, it)?.let { it1 -> bitmapList.add(it1) }
                        } else {
                            // Handle the error (e.g., display an error message)
                        }
                    }
                    viewModel.sendPrompt(
                        bitmapList,
                        "can you return what is the object in this images in one word"
                    )
                }) {
                Text(text = "Detect Object")
            }
        }
        when (uiState) {
            is UiState.Error -> {
                Text(text = (uiState as UiState.Error).errorMessage)
            }

            UiState.Initial -> {}
            UiState.Loading -> {
                CircularProgressIndicator(
                    context
                )
            }

            is UiState.Success<*> -> {
                Text(text = (uiState as UiState.Success<*>).outputData as String)
            }
        }
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
fun ImageOfUri(uri: Uri, uriId: Int, viewModel: FoundItemViewModel = viewModel()) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            modifier = Modifier
                .padding(5.dp)
                .width(128.dp)
                .wrapContentHeight(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.captured_image),
        )
        // Add close button on top left
        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp), // Adjust padding as needed
            onClick = { viewModel.list.removeAt(uriId) }
        ) {
            Image(
                painterResource(id = R.drawable.close),
                contentDescription = stringResource(R.string.delete_image)
            ) // Replace with your desired icon
        }
    }
}