package com.fstech.myItems.presentation.found

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.jetawy.domain.models.ItemResponse

@Composable
fun EnterDataOfFoundItemScreen(navController: () -> Unit, name: String, description: String, color: List<String>, brand: String, category: String
) {
Text(text = name.toString())

}