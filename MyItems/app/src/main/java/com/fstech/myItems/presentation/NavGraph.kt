package com.fstech.myItems.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fstech.myItems.presentation.found.EnterDataOfFoundItemScreen
import com.fstech.myItems.presentation.found.FoundItemScreen
import com.fstech.myItems.presentation.lost.LostItemScreen
import com.fstech.myItems.presentation.welcome.WelcomeScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Screen.WelcomeScreen.route) {

        composable(route = Screen.HomeScreen.route) { HomeScreen(navController = navController) }

        composable(route = Screen.MapScreen.route) { MapScreen(navController = navController) }

        composable(route = Screen.BakingScreen.route) { BakingScreen(navController = navController) }

        composable(route = Screen.WelcomeScreen.route) { WelcomeScreen(navController = navController) }

        composable(route = Screen.FoundItemScreen.route) { FoundItemScreen(navController = navController) }

        composable(route = Screen.LostItemScreen.route) { LostItemScreen(navController = navController) }

        composable(route = Screen.EnterDataOfFoundItemScreen.route) { EnterDataOfFoundItemScreen(navController = navController) }

    }
}