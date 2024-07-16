package com.fstech.myItems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =  Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Pickup a Service")

        Button(onClick = { navController.navigate(Screen.FoundItemScreen.route) }) {
            Text(text = "I Found An Item")
        }

        Button(onClick = { navController.navigate(Screen.LostItemScreen.route) }) {
            Text(text = "I Lost An Item")
        }
        Button(onClick = { navController.navigate(Screen.MapScreen.route) }) {
            Text(text = "Search Items Casually")
        }
    }

}



