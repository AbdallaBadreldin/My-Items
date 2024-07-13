package com.fstech.myItems

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fstech.myItems.ui.theme.HomeScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {

        composable(route = Screen.MainScreen.route) { HomeScreen(navController) }

        composable(route = Screen.DetailsScreen.route) { BakingScreen(navController=navController)  }


    }
}