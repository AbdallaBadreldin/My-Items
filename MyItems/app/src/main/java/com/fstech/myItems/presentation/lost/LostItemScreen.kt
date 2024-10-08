package com.fstech.myItems.presentation.lost

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fstech.myItems.R
import com.fstech.myItems.presentation.found.StringInputTextField
import com.jetawy.domain.utils.Converter
import com.jetawy.domain.utils.UiState

@Composable
fun LostItemScreen(gotoLocationOfLostItems: () -> Unit, viewModel: LostItemViewModel) {
    val scrollState = rememberScrollState()
    val options = listOf(
        stringResource(R.string.left_it),
        stringResource(R.string.stolen),
        stringResource(R.string.hijacked), stringResource(R.string.snatched),
        stringResource(R.string.unknown), stringResource(R.string.other)
    )
    val context = LocalContext.current

    val showErrorName = remember { mutableStateOf("") }
    val showErrorModel = remember { mutableStateOf("") }
    val showErrorBrand = remember { mutableStateOf("") }
    val showErrorColor1 = remember { mutableStateOf("") }
    val showErrorColor2 = remember { mutableStateOf("") }
    val showErrorColor3 = remember { mutableStateOf("") }
    val showErrorUserDescription = remember { mutableStateOf("") }

    Column(
        Modifier
            .verticalScroll(scrollState)
            .fillMaxWidth()
    ) {

        PhotoSelectorView(maxSelectionCount = 3, viewModel)

        //show selected images
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
                .wrapContentHeight(),
        ) {
            itemsIndexed(viewModel.list) { index, item ->
                ImageOfUri(item, index, viewModel)
            }
        }
        SingleLineInputTextField(
            value = viewModel.type.value ?: "",
            onValueChange = {
                viewModel.type.value = it
                showErrorName.value = ""
            },
            label = stringResource(R.string.type_e_g_watch_card),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            showError = showErrorName.value
        )
        SingleLineInputTextField(
            value = viewModel.model.value ?: "",
            onValueChange = {
                viewModel.model.value = it
                showErrorModel.value = ""
            },
            label = "model",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            showError = showErrorModel.value
        )
        SingleLineInputTextField(
            value = viewModel.brand.value ?: "",
            onValueChange = {
                viewModel.brand.value = it
                showErrorBrand.value = ""
            },
            label = stringResource(R.string.brand),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            showError = showErrorBrand.value
        )
        Row {
            SingleLineInputTextField(
                value = viewModel.color1.value ?: "",
                onValueChange = {
                    viewModel.color1.value = it
                    showErrorColor1.value = ""
                },
                label = stringResource(R.string.first_color),
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1F),
                showError = showErrorColor1.value
            )
            SingleLineInputTextField(
                value = viewModel.color2.value ?: "",
                onValueChange = {
                    viewModel.color2.value = it
                    showErrorColor2.value = ""
                },
                label = stringResource(R.string.second_color),
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1F),
                showError = showErrorColor2.value
            )
            SingleLineInputTextField(
                value = viewModel.color3.value ?: "",
                onValueChange = {
                    viewModel.color3.value = it
                    showErrorColor3.value = ""
                },
                label = stringResource(R.string.third_color),
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1F),
                showError = showErrorColor3.value
            )
        }

        StringInputTextField(
            value = viewModel.userDescription.value ?: "",
            onValueChange = {
                viewModel.userDescription.value = it
                showErrorUserDescription.value = ""
            },
            label = stringResource(R.string.can_you_tell_us_more_details_or_description_of_the_item_to_find_it_faster),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            showError = showErrorUserDescription.value
        )


        Text(
            text = stringResource(R.string.state), modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Spinner(
            onValueChange = { viewModel.itemState.value = it },
            options,
        )

        if (viewModel.itemState.value == options[options.size - 1]) {
            SingleLineInputTextField(
                value = viewModel.itemState.value ?: "",
                onValueChange = { viewModel.itemState.value = it },
                label = stringResource(R.string.state),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )
            LaunchedEffect(key1 = Unit) {
                scrollState.scrollTo(scrollState.maxValue)
            }
        }

        if (viewModel.userDescriptionError.value?.isNotEmpty() == true) {
            Text(
                text = viewModel.userDescriptionError.value!!, modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
        MyCheckbox { viewModel.strictDescription.value = it }
        when (viewModel.uiState.collectAsState().value) {
            is UiState.Error -> {
                Toast.makeText(
                    context,
                    (viewModel.uiState.collectAsState().value as UiState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
                Log.e(
                    "LostItemScreen",
                    (viewModel.uiState.collectAsState().value as UiState.Error).message
                )
                viewModel.resetStates()
            }

            UiState.Initial -> {
                Button(
                    onClick = {
                        if (viewModel.color1.value.toString().trimIndent().trim()
                                .isEmpty() && viewModel.color2.value.toString().trimIndent().trim()
                                .isNotEmpty()
                        ) {
                            showErrorColor2.value =
                                context.getString(R.string.first_color_is_required)
                        } else showErrorColor2.value = ""

                        if (viewModel.color1.value.toString().trimIndent().trim()
                                .isEmpty() && viewModel.color3.value.toString().trimIndent().trim()
                                .isNotEmpty()
                        ) {
                            showErrorColor3.value =
                                context.getString(R.string.first_color_is_required)
                        } else showErrorColor3.value = ""

                        if (viewModel.type.value.toString().trimIndent().trim().isEmpty())
                            showErrorName.value =
                                context.getString(R.string.this_field_cannot_be_empty)
                        else
                            showErrorName.value = ""
                        if (viewModel.model.value.toString().trimIndent().trim().isEmpty())
                            showErrorModel.value =
                                context.getString(R.string.this_field_cannot_be_empty)
                        else
                            showErrorModel.value = ""
                        if (viewModel.brand.value.toString().trimIndent().trim().isEmpty())
                            showErrorBrand.value =
                                context.getString(R.string.this_field_cannot_be_empty)
                        else
                            showErrorBrand.value = ""
                        if (viewModel.color1.value.toString().trimIndent().trim().isEmpty())
                            showErrorColor1.value =
                                context.getString(R.string.this_field_cannot_be_empty)
                        else {
                            showErrorColor1.value = ""
                            showErrorColor2.value = ""
                            showErrorColor3.value = ""
                        }
                        if (viewModel.userDescription.value.toString().trimIndent().trim()
                                .isEmpty()
                        )
                            showErrorUserDescription.value =
                                context.getString(R.string.this_field_cannot_be_empty)
                        else
                            showErrorUserDescription.value = ""

                        if (showErrorName.value.trimIndent().trim().isEmpty() &&
                            showErrorModel.value.trimIndent().trim().isEmpty() &&
                            showErrorBrand.value.trimIndent().trim().isEmpty() &&
                            showErrorColor1.value.trimIndent().trim().isEmpty() &&
                            showErrorUserDescription.value.trimIndent().trim().isEmpty()
                        ) {
                            val contentResolver = context.contentResolver
                            val bitmapList = mutableListOf<Bitmap>()
                            viewModel.list.forEach {
                                if (it != null || it != Uri.EMPTY) {
                                    Converter().uriToBitmap(contentResolver, it)?.let { it1 ->
                                        bitmapList.add(
                                            it1
                                        )
                                    }
                                } else {
                                    // Handle the error (e.g., display an error message)
                                }
                            }
                            viewModel.sendPrompt(bitmapList)

//need to send prompt to AI to ask for category
//                    then ask if inputs are right
//                    then ask for images don't contain bad things
//                    then ask fro information anout images and translation of user inputs
                        }

                    }, modifier = Modifier
                        .padding(64.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(stringResource(R.string.next))
                }
            }

            UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(64.dp)
                )
            }

            is UiState.Success<*> -> {
                gotoLocationOfLostItems()
            }
        }

    }
}

@Composable
fun PhotoSelectorView(maxSelectionCount: Int = 1, viewModel: LostItemViewModel) {
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.addList(uri) }
    )

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = if (maxSelectionCount > 1) {
                maxSelectionCount
            } else {
                2
            }
        ),
        onResult = { uris -> viewModel.addList(uris) }
    )

    fun launchPhotoPicker() {
        if (maxSelectionCount > 1) {
            multiplePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } else {
            singlePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = {
                launchPhotoPicker()
            }, modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            val buttonText = if (maxSelectionCount > 1) {
                stringResource(R.string.select_up_to_photos, maxSelectionCount)
            } else {
                stringResource(R.string.select_a_photo)
            }
            Text(buttonText)
        }


    }
}

@Composable
fun ImageOfUri(uri: Uri, uriId: Int, viewModel: LostItemViewModel) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            modifier = Modifier
                .padding(5.dp)
                .width(128.dp)
                .wrapContentHeight(),
            model = ImageRequest.Builder(context)
                .data(uri)
                .error(R.drawable.round_add_circle_outline_24)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.selected_image),
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
fun SingleLineInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    showError: String = "",
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        supportingText = {
            if (showError.isNotEmpty()) {
                Text(
                    text = showError,
                    color = Color.Red
                )
            }
        },
        singleLine = true // Ensures single-line input
    )
}

@Composable
fun Spinner(onValueChange: (String) -> Unit, options: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[0]) }
    LaunchedEffect(key1 = "selectedOptionText") { onValueChange(selectedOptionText)}
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterHorizontally)
        ) {
            Text(selectedOptionText, modifier = Modifier.wrapContentSize())
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = stringResource(id = R.string.state),
            )
        }
        DropdownMenu(
            modifier = Modifier
                .wrapContentSize()
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { label ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            modifier = Modifier
                                .wrapContentSize()
                                .wrapContentHeight()
                                .align(Alignment.CenterHorizontally)
                        )
                    },
                    onClick = {
                        expanded = false
                        selectedOptionText = label
                        onValueChange(label)
                    }
                )
            }
        }
    }
}

@Composable
fun MyCheckbox(onValueChange: (Boolean) -> Unit) {
    var isChecked by remember { mutableStateOf(true) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                onValueChange(it)
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = if (isChecked) stringResource(R.string.more_strict) else stringResource(R.string.less_strict))
    }
    Text(
        text = stringResource(R.string.more_strict_for_better_matching),
        modifier = Modifier.padding(16.dp, 0.dp)
    )
}