package com.fstech.myItems.presentation.found

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

@Composable
fun EnterDataOfFoundItemScreen(
    navController: () -> Unit,
    viewModel: FoundItemViewModel
) {
    Text(text = viewModel.list.toList().toString())
    Text(text = viewModel.uiState.collectAsState().value.toString())

}