package com.fstech.myItems.presentation.matchmaking

import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fstech.myItems.R
import com.google.gson.Gson
import com.jetawy.domain.models.get.found.ItemFoundResponse
import com.jetawy.domain.models.get.lost.ItemLostResponse
import com.jetawy.domain.utils.UiState
import java.util.Locale

const val topItemsCount = 10

@Composable
fun MatchMakingScreen(goToMatchDetailsScreen: () -> Unit, viewModel: MatchMakingViewModel) {
    val context = LocalContext.current
    val getFoundUiStateByCountry = viewModel.getFoundUiStateByCountry.collectAsState()
    val prompt = viewModel.promptState.collectAsState()
    val countryName = remember {
        mutableStateOf("")
    }
    val currentItem =
        (viewModel.lostUiState.collectAsState().value as UiState.Success<MutableList<ItemLostResponse>>).outputData[viewModel.itemIndex] //details and index of current found lost item

    when (getFoundUiStateByCountry.value) {
        is UiState.Error -> {
            Button(
                onClick = {
                    viewModel.getFoundItemByCountry(countryName.value)
                }, modifier = Modifier
                    .wrapContentSize()
            ) {
                Text(text = stringResource(R.string.retry), modifier = Modifier.wrapContentSize())
            }
        }

        UiState.Initial -> {
            LaunchedEffect(key1 = "dataFetchKey") {
                val location = currentItem.location
                val locations = location?.split(",")
                val lat = locations?.get(0)?.toDouble() ?: 0.0
                val lng = locations?.get(1)?.toDouble() ?: 0.0
                val geocoder = Geocoder(context, Locale.ENGLISH)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(lat, lng, 1)
                    { addresses ->
                        countryName.value = addresses[0].countryName
                        viewModel.lostItemCountryName = addresses[0].countryName
                        viewModel.getFoundItemByCountry(countryName.value)
                    }
                } else {
                    val addresses = geocoder.getFromLocation(lat, lng, 1)
                    countryName.value = addresses?.get(0)?.countryName.toString()
                    viewModel.lostItemCountryName = addresses?.get(0)?.countryName.toString()
                    viewModel.getFoundItemByCountry(countryName.value)
                }
            }
        }

        UiState.Loading -> {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.loading),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                )
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(128.dp)
                )
            }
        }

        is UiState.Success<*> -> {
            val listOfFoundItems =
                viewModel.getFoundUiStateByCountry.value as UiState.Success<MutableList<ItemFoundResponse>>
            val listOfFoundItems2 = listOfFoundItems.outputData
            if (listOfFoundItems2.isEmpty()) {
                Text(
                    text = stringResource(R.string.failed_to_find_matched_items),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(64.dp),
                )
                Button(
                    onClick = {
                        viewModel.getFoundItemByCountry(countryName.value)
                    }, modifier = Modifier
                        .wrapContentSize()
                ) {
                    Text(
                        text = stringResource(R.string.retry),
                        modifier = Modifier.wrapContentSize()
                    )
                }
                return
            } else
                LaunchedEffect("startTheMainTasks") {
                    viewModel.lostItemId =
                        currentItem.objectID.toString() //should be current item lost id
                    Log.e("TAG لا", "MatchMakingScreen: ${currentItem}")
                    Log.e("TAG لا", "MatchMakingScreen: ${listOfFoundItems}")
                    val currentItemJson = Gson().toJson(currentItem)
                    val listOfFoundItemsJson = Gson().toJson(listOfFoundItems)
                    viewModel.sendPrompt(
                        prompt = "can you make the best matching item for next object **$currentItemJson** with the following array of objects **$listOfFoundItemsJson** return best $topItemsCount objects matching as json array and don't return first object with the returned array"
                    )
                }
        }
    }
    when (prompt.value) {
        is UiState.Error -> {
            Button(onClick = {
                viewModel.resetPromptState()
                viewModel.getFoundItemByCountry(countryName.value)
            }) {
                Text(text = stringResource(R.string.retry), modifier = Modifier.wrapContentSize())
            }
        }

        UiState.Initial -> {}
        UiState.Loading -> {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.loading),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                )
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(128.dp)
                )
            }
        }

        is UiState.Success<*> -> {
            val listOfMatchedItems =
                (prompt.value as UiState.Success<List<ItemFoundResponse>>).outputData

            //should show list of matched items
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(listOfMatchedItems.size) { index ->
                    ItemColumn(
                        listOfMatchedItems[index],
                        index,
                        goToMatchDetailsScreen,
                        viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ItemColumn(
    data: ItemFoundResponse,
    index: Int,
    goToMatchDetailsScreen: () -> Unit,
    viewModel: MatchMakingViewModel
) {
    Column(
        Modifier
            .padding(64.dp)
            .border(2.dp, Color.Red)
            .fillMaxWidth()
            .background(Color.White)
            .clip(RoundedCornerShape(16.dp))
            .clickable { },
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${index + 1}",
            modifier = Modifier.padding(16.dp),
            color = Color.Black
        )
        Text(
            text = (stringResource(R.string.type) + data.aiResponse?.type),
            modifier = Modifier.padding(16.dp),
            color = Color.Black
        )
        if (!data.aiResponse?.userDescription.isNullOrEmpty())
            Text(
                text = (stringResource(R.string.description) + data.aiResponse?.userDescription),
                modifier = Modifier.padding(16.dp),
                color = Color.Black
            )
        else
            Text(
                text = stringResource(R.string.no_description),
                modifier = Modifier.padding(16.dp),
                color = Color.Black
            )
        if (!data.images.isNullOrEmpty()) {
            AsyncImage(
                model = data.images?.get(0),
                contentDescription = data.aiResponse?.userDescription
                    ?: stringResource(id = R.string.no_description),
                modifier = Modifier
                    .padding(16.dp)
                    .size(128.dp)
                    .clip(
                        RoundedCornerShape(16.dp)
                    )
            )
        } else {
            Text(
                text = stringResource(id = R.string.no_images),
                modifier = Modifier.padding(16.dp),
                color = Color.Black
            )
        }
        Button(onClick = {
            viewModel.detailIndex = index
            goToMatchDetailsScreen()
        }, modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.view_item_details),
                modifier = Modifier
            )
        }
    }

}