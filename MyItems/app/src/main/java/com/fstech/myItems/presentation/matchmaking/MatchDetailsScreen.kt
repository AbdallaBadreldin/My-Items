package com.fstech.myItems.presentation.matchmaking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fstech.myItems.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jetawy.domain.models.get.found.ItemFoundResponse
import com.jetawy.domain.utils.UiState

@Composable
fun MatchDetailsScreen(goToMatchMakingSuccessScreen: () -> Unit, viewModel: MatchMakingViewModel) {
    val prompt = viewModel.promptState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    ConfirmationDialog(
        message = stringResource(R.string.it_s_mine),
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onConfirm = {
            val listOfMatchedItems =
                (viewModel.promptState.value as UiState.Success<List<ItemFoundResponse>>).outputData
            val currentItem = listOfMatchedItems[viewModel.detailIndex]
            viewModel.createChatRoom(
                it,
                Firebase.auth.currentUser?.uid.toString(),
                currentItem.user.toString(),
                currentItem.objectID.toString(),//foundItem
                viewModel.lostItemCountryName,
                viewModel.lostItemId,
                viewModel.lostItemCountryName,
                currentItem.aiResponse?.type.toString()
            )
            showDialog = false
        },
        title = stringResource(R.string.confirm_action),
        text = stringResource(R.string.are_you_sure_you_want_to_proceed)
    )
    when (prompt.value) {

        is UiState.Error -> {}
        UiState.Initial -> {}
        UiState.Loading -> {}
        is UiState.Success<*> -> {
            val listOfMatchedItems =
                (viewModel.promptState.collectAsState().value as UiState.Success<List<ItemFoundResponse>>).outputData
            val currentItem = listOfMatchedItems[viewModel.detailIndex]
            Column(
                Modifier
                    .verticalScroll(state = rememberScrollState())
                    .wrapContentSize()
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.CenterHorizontally)
                ) {
                    if (currentItem.images != null) {
                        if ((currentItem.images?.size ?: 0) >= 1) {
                            AsyncImage(
                                modifier = Modifier.weight(1f),
                                model = currentItem.images?.get(0),
                                contentDescription = currentItem.aiResponse?.imageDescription
                            )
                        }
                        if ((currentItem.images?.size ?: 0) >= 2) {
                            AsyncImage(
                                modifier = Modifier.weight(1f),
                                model = currentItem.images?.get(1),
                                contentDescription = currentItem.aiResponse?.imageDescription
                            )
                        }
                        if ((currentItem.images?.size ?: 0) >= 3) {
                            AsyncImage(
                                modifier = Modifier.weight(1f),
                                model = currentItem.images?.get(2),
                                contentDescription = currentItem.aiResponse?.imageDescription
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(16.dp))
                Text("Type: ${currentItem.aiResponse?.type}")
                Text("Description: ${currentItem.aiResponse?.userDescription ?: stringResource(R.string.no_description)}")
//        Text("Image Description: ${currentItem.imageDescription}")
//                Text("Item State: ${currentItem.aiResponse?.itemState}")
//        Text("Translated Description: ${currentItem.aiResponse?.translatedDescription}")
                Text("Model: ${currentItem.aiResponse?.model}")
                Text("Category: ${currentItem.aiResponse?.category}")
                Text("Brand: ${currentItem.aiResponse?.brand}")
                Text("Colors: ${currentItem.aiResponse?.colors?.joinToString(", ")}")
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = stringResource(R.string.it_s_mine))
                }

            }
        }
    }

    /*
      // 1 send message to user that some one own this
      // 2 navigate to chat screen
      // 3 delete it from database and profile
      // 4 notify another user that it is mine
  }*/
    when (viewModel.createChatRoom.collectAsState().value) {
        is UiState.Error -> {
            Text(text = stringResource(id = R.string.retry))
        }

        UiState.Initial -> {
        }

        UiState.Loading -> {
            CircularProgressIndicator()
        }

        is UiState.Success<*> -> {
            goToMatchMakingSuccessScreen()
        }
    }
}

@Composable
fun ConfirmationDialog(
    message: String,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (string: String) -> Unit,
    title: String,
    text: String
) {
    if (showDialog) {
        AlertDialog(onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(text) },
            confirmButton = {
                Button(onClick = { onConfirm(message) }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

}

