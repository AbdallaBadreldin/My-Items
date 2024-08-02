package com.fstech.myItems.presentation.matchmaking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.jetawy.domain.utils.UiState

@Composable
fun ShowItemsScreen(viewModel: MatchMakingViewModel) {

    LaunchedEffect(key1 = "dataFetchKey") {
        viewModel.getFoundItemData()
        viewModel.getLostItemData()
    }

    when (viewModel.foundUiState.collectAsState().value) {
        is UiState.Error -> {}//TODO()
        UiState.Initial -> {}//TODO()
        UiState.Loading -> {}//TODO()
        is UiState.Success<*> -> {}// TODO()
    }
    when (viewModel.lostUiState.collectAsState().value) {
        is UiState.Error -> {}//TODO()
        UiState.Initial -> {}//TODO()
        UiState.Loading -> {}//TODO()
        is UiState.Success<*> -> {}// TODO()
    }
}