package com.fstech.myItems.presentation.found

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fstech.myItems.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

const val maxImages = 5

@OptIn(ExperimentalCoilApi::class)
@Composable
fun FoundItemScreen(navController: NavController, viewModel: FoundItemViewModel = viewModel()) {

    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            if (viewModel.list.size < maxImages) {
                viewModel.addItem(uri)
            }
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            if (viewModel.list.size < maxImages)
                cameraLauncher.launch(uri)
            else {
                Toast.makeText(
                    context,
                    "You can only take $maxImages images",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val permissionCheckResult =
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                if (viewModel.list.size < maxImages)
                    cameraLauncher.launch(uri)
                else {
                    Toast.makeText(
                        context,
                        "You can only take $maxImages images",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // Request a permission
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }) {
            Text(text = "Capture Image From Camera")
        }
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
//    val capturedImages = remember { viewModel.capturedImageUri.value }

//    if (viewModel.list.isNotEmpty()) {

//    }
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
    Box(modifier = Modifier.padding(5.dp)) {
        Button(onClick = {
            viewModel.list.removeAt(uriId)
        }) {
//           I want to create X above the AsyncImage
//            Image(painter =, contentDescription =)
        }

        AsyncImage(
            modifier = Modifier
                .padding(5.dp)
                .width(128.dp)
                .wrapContentHeight(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .crossfade(true)
                .build(),
            contentDescription = "Image from URI"
        )
    }
}