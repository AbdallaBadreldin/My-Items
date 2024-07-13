package com.fstech.myItems.ui.theme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.rememberNavController
import com.fstech.myItems.NavGraph
import com.fstech.myItems.Screen
import com.fstech.myItems.ui.theme.ui.theme.MyItemsTheme
@Composable
fun HomeScreen(  navController: NavHostController)   {

            MyItemsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text(
                        text = "Android",
                        modifier = Modifier.padding(innerPadding).clickable {
                            navController.navigate(Screen.DetailsScreen.route){
                                popUpTo(Screen.MainScreen.route){
                                    inclusive=true
                                }
                            }
                        }
                    )
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