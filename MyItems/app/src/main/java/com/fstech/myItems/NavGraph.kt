package com.fstech.myItems

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Screen.WelcomeScreen.route) {

        composable(route = Screen.MainScreen.route) { HomeScreen(navController) }

        composable(route = Screen.DetailsScreen.route) { BakingScreen(navController=navController)  }

        composable(route = Screen.WelcomeScreen.route) { WelcomeScreen(navController=navController)  }

        composable(route = Screen.MapScreen.route) { BakingScreen(navController=navController)  }


    }
}