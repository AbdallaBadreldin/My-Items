package com.fstech.myItems.presentation.matchmaking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.fstech.myItems.R
import com.jetawy.domain.models.get.found.ItemFoundResponse
import com.jetawy.domain.utils.UiState

@Composable
fun MatchDetailsScreen(viewModel: MatchMakingViewModel) {
    val listOfMatchedItems =
        (viewModel.promptState.collectAsState().value as UiState.Success<List<ItemFoundResponse>>).outputData
    val currentItem = listOfMatchedItems[viewModel.detailIndex]
    Column(Modifier.verticalScroll(state = rememberScrollState())) {
        if (!currentItem.images.isNullOrEmpty()) {
            if ((currentItem.images?.size ?: 0) >= 1) {
                AsyncImage(
                    model = currentItem.images?.get(0),
                    contentDescription = currentItem.aiResponse?.imageDescription
                )
            }
            if ((currentItem.images?.size ?: 0) >= 2) {
                AsyncImage(
                    model = currentItem.images?.get(1),
                    contentDescription = currentItem.aiResponse?.imageDescription
                )
            }
            if ((currentItem.images?.size ?: 0) >= 3) {
                AsyncImage(
                    model = currentItem.images?.get(2),
                    contentDescription = currentItem.aiResponse?.imageDescription
                )
            }
        }
        Text("Type: ${currentItem.aiResponse?.type}")
        Text("Description: ${currentItem.aiResponse?.userDescription}")
//        Text("Image Description: ${currentItem.imageDescription}")
        Text("Item State: ${currentItem.aiResponse?.itemState}")
//        Text("Translated Description: ${currentItem.aiResponse?.translatedDescription}")
        Text("Model: ${currentItem.aiResponse?.model}")
        Text("Category: ${currentItem.aiResponse?.category}")
        Text("Brand: ${currentItem.aiResponse?.brand}")
        Text("Colors: ${currentItem.aiResponse?.colors?.joinToString(", ")}")
  Button(onClick = {
      // 1 send message to user that some one own this
      // 2 navigate to chat screen
      // 3 delete it from database and profile
      // 4 notify another user that it is mine
  }) {
      Text(text = stringResource(R.string.it_s_mine))
  }
    }

}