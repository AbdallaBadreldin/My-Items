package com.fstech.myItems.presentation.matchmaking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.fstech.myItems.R
import com.jetawy.domain.models.get.found.ItemFoundResponse
import com.jetawy.domain.models.get.lost.ItemLostResponse
import com.jetawy.domain.utils.UiState

@Composable
fun ShowItemsScreen(goToMatchMakingScreen: () -> Unit, viewModel: MatchMakingViewModel) {

    LaunchedEffect(key1 = "dataFetchKey") {
        viewModel.getFoundItemById()
        viewModel.getLostItemById()
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (viewModel.foundUiState.collectAsState().value) {
            is UiState.Error -> {
                Button(onClick = { viewModel.getFoundItemById() }) {
                    Text(text = stringResource(R.string.retry))
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
                val response = viewModel.foundUiState.collectAsState().value as UiState.Success<*>
                val data = response.outputData as MutableList<ItemFoundResponse>
                Text(
                    text = stringResource(R.string.found_items), modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )
                if (data.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_found_items),
                        modifier = Modifier
                            .padding(0.dp, 64.dp)
                            .wrapContentSize()
                            .align(Alignment.CenterHorizontally),
                        color = Color.Red,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {

                    LazyRow(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        items(data.size) { index ->
                            ItemRow(data[index], index + 1)
                        }
                    }
                }
            }
        }
        when (viewModel.lostUiState.collectAsState().value) {
            is UiState.Error -> {
                Button(onClick = { viewModel.getLostItemById() }) {
                    Text(text = stringResource(R.string.retry))
                }
            }

            UiState.Initial -> {}//TODO()
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
                val response = viewModel.lostUiState.collectAsState().value as UiState.Success<*>
                val data = response.outputData as MutableList<ItemLostResponse>
                Text(
                    text = stringResource(R.string.lost_items), modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )
                if (data.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_lost_items),
                        modifier = Modifier
                            .padding(0.dp, 64.dp)
                            .wrapContentSize()
                            .align(Alignment.CenterHorizontally),
                        color = Color.Red,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    LazyRow(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        items(data.size) { index ->
                            ItemRow(data[index], index + 1, goToMatchMakingScreen, viewModel)
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun ItemRow(
    data: ItemLostResponse,
    index: Int,
    goToMatchMakingScreen: () -> Unit,
    viewModel: MatchMakingViewModel
) {
    Column(
        Modifier
            .padding(64.dp)
            .border(2.dp, Color.Red)
            .background(Color.White)
            .clip(RoundedCornerShape(16.dp))
            .clickable { }) {
        Text(
            text = "$index",
            modifier = Modifier.padding(16.dp),
            color = Color.Black
        )
        Text(
            text = (stringResource(R.string.type) + data.aiResponse?.type),
            modifier = Modifier.padding(16.dp),
            color = Color.Black
        )
        Text(
            text = (stringResource(R.string.description) + data.aiResponse?.userDescription),
            modifier = Modifier.padding(16.dp),
            color = Color.Black
        )
        if (!data.images.isNullOrEmpty()) {
            AsyncImage(
                model = data.images?.get(0),
                contentDescription = data.aiResponse?.userDescription ?: "",
                modifier = Modifier
                    .padding(16.dp)
                    .size(64.dp)
                    .clip(
                        RoundedCornerShape(16.dp)
                    )
            )
        }
        Text(
            text = stringResource(id = R.string.no_images),
            modifier = Modifier.padding(16.dp),
            color = Color.Black
        )
        Button(onClick = {
            viewModel.itemIndex = index
            goToMatchMakingScreen()
        }, modifier = Modifier) {
            Text(
                text = stringResource(R.string.start_matching),
                modifier = Modifier
            )
        }
    }

}

@Composable
fun ItemRow(data: ItemFoundResponse, index: Int) {
    Column(
        Modifier
            .padding(64.dp)
            .border(1.dp, Color.Red)
            .background(Color.White)
            .clip(RoundedCornerShape(16.dp))
            .clickable { }) {
        Text(
            text = "$index",
            modifier = Modifier.padding(16.dp),
            color = Color.Black
        )
        Text(
            text = (stringResource(R.string.type) + data.aiResponse?.type),
            modifier = Modifier.padding(16.dp),
            color = Color.Black
        )
        Text(
            text = (stringResource(R.string.description) + data.aiResponse?.userDescription),
            modifier = Modifier.padding(16.dp),
            color = Color.Black
        )
        if (!data.images.isNullOrEmpty()) {
            AsyncImage(
                model = data.images?.get(0),
                contentDescription = data.aiResponse?.userDescription ?: "",
                modifier = Modifier
                    .padding(16.dp)
                    .size(64.dp)
                    .clip(
                        RoundedCornerShape(16.dp)
                    )
            )
        }
        Text(
            text = stringResource(R.string.no_images),
            modifier = Modifier.padding(16.dp),
            color = Color.Black
        )
    }
}