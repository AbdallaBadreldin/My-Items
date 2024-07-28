package com.fstech.myItems.presentation.found

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstech.myItems.R

@Composable
fun EnterDataOfFoundItemScreen(
    navController: () -> Unit,
    viewModel: FoundItemViewModel
) {
    var inputText by remember { mutableStateOf("") }

    StringInputTextField(
        value = inputText,
        onValueChange = { inputText = it },
        label = "Can you tell us more details or description of the item to help us matching it to it's owner faster?"
    )
Button(
    modifier = Modifier
        .fillMaxWidth()
        .padding(32.dp),
    onClick = {viewModel.uploadItem() }) { Text(text = stringResource(R.string.upload_data)) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StringInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        singleLine = true, // For single-line input
        shape = RoundedCornerShape(16.dp), // Rounded corners for a softer look
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}