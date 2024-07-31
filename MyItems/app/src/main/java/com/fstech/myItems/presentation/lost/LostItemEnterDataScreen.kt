package com.fstech.myItems.presentation.lost

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.fstech.myItems.R
import com.fstech.myItems.presentation.found.circularProgressIndicator
import com.jetawy.domain.models.ItemFound
import com.jetawy.domain.models.ItemLost

@Composable
fun LostItemEnterDataScreen(function: () -> Unit, viewModel: LostItemViewModel) {
    Text(text = stringResource(R.string.uploading_your_item))
    circularProgressIndicator()
    val itemLost = ItemLost(
        name = viewModel.name.value,
        model = viewModel.model.value,
        brand = viewModel.brand.value,
        category = viewModel.category.value,
        itemState = viewModel.itemState.value,
        colors = viewModel.colors.value,
        description = viewModel.userDescription.value
    )
    viewModel.uploadItems(
        imageUris = viewModel.list,
        addresses = viewModel.addresses!![0],
        aiResponse =itemLost,
        userDescription = viewModel.userDescription.value
    )
}


