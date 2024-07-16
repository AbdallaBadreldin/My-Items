package com.fstech.myItems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.fstech.myItems.ui.theme.MyItemsTheme

@Composable
fun HomeScreen(navController: NavHostController) {

    MyItemsTheme {
        Scaffold(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) { innerPadding ->
        Column {
            Text(text = "Pickup a Service")
            Button(onClick = { navController.navigate(Screen.FoundItemScreen.route) }) {
                Text(text = "I Found Item")
            }

            Button(onClick = { navController.navigate(Screen.LostItemScreen.route) }) {
            Text(text = "I Lost Item")
            }

        }
            Text(text = "Android", modifier = Modifier
                .padding(innerPadding)
                .clickable {
                    navController.navigate(Screen.BakingScreen.route) {
                        popUpTo(Screen.MainScreen.route) {
                            inclusive = true
                        }
                    }
                })
        }
    }


    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier.clickable {

            },
        )
    }

}