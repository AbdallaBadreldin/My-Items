package com.fstech.myItems.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fstech.myItems.presentation.HomeScreen
import com.fstech.myItems.presentation.found.EnterDataOfFoundItemScreen
import com.fstech.myItems.presentation.found.FoundItemScreen
import com.fstech.myItems.presentation.found.FoundItemViewModel
import com.fstech.myItems.presentation.found.LocationOfLostItem
import com.fstech.myItems.presentation.lost.LostItemScreen
import com.fstech.myItems.presentation.welcome.WelcomeScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = NavRoute.WelcomeNavRoute.path) {
        openWelcomeScreen(navController = navController, this)
        openLostItemScreen(navController = navController, this)
        openMainScreen(navController = navController, this)
        openHomeScreen(navController = navController, this)
        openMapScreen(navController = navController, this)
//        composable(route = NavRoute.BakingNavRoute.path) { BakingScreen(navController = navController) }
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: FoundItemViewModel
) {
    NavHost(navController = navController, startDestination = NavRoute.FoundItemNavRoute.path) {
        openFoundItemScreen(navController = navController, this, viewModel = viewModel)
        openLocationFoFoundItemScreen(navController = navController, this, viewModel = viewModel)

        openEnterDataOfFoundItemScreen(navController = navController, this, viewModel)
    }

}

fun openLocationFoFoundItemScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    viewModel: FoundItemViewModel
) {
    navGraphBuilder.composable(
        route = NavRoute.LocationOfLostItemNavRoute.path
    ) {
        LocationOfLostItem(
            navigateToEnterDataOfFoundItemScreen = { navController.navigate(NavRoute.EnterDataOfFoundItemNavRoute.path) },
            viewModel = viewModel
        )
    }
}

fun openEnterDataOfFoundItemScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    viewModel: FoundItemViewModel
) {
    navGraphBuilder.composable(
        route = NavRoute.EnterDataOfFoundItemNavRoute.path
    ) {
        /* route = NavRoute.EnterDataOfFoundItemNavRoute.withArgsFormat(
         NavRoute.EnterDataOfFoundItemNavRoute.name,
           NavRoute.EnterDataOfFoundItemNavRoute.description,
           NavRoute.EnterDataOfFoundItemNavRoute.color,
           NavRoute.EnterDataOfFoundItemNavRoute.brand,
           NavRoute.EnterDataOfFoundItemNavRoute.category
        ),*/
        /*   arguments = listOf
               (
               navArgument(NavRoute.EnterDataOfFoundItemNavRoute.name) {
                   type = NavType.StringType
               },
               navArgument(NavRoute.EnterDataOfFoundItemNavRoute.description) {
                   type = NavType.StringType
               },
               navArgument(NavRoute.EnterDataOfFoundItemNavRoute.color) {
                   type = NavType.StringType
               },
               navArgument(NavRoute.EnterDataOfFoundItemNavRoute.brand) {
                   type = NavType.StringType
               },
               navArgument(NavRoute.EnterDataOfFoundItemNavRoute.category) {
                   type = NavType.StringType
               },


               )
       ) {*/
        /*   entry ->
           val args = entry.arguments
           val name = args?.getString(NavRoute.EnterDataOfFoundItemNavRoute.name).toString()
           val description = args?.getString(NavRoute.EnterDataOfFoundItemNavRoute.description).toString()
           val color = args?.getStringArrayList(NavRoute.EnterDataOfFoundItemNavRoute.color)
           val brand = args?.getString(NavRoute.EnterDataOfFoundItemNavRoute.brand).toString()
           val category = args?.getString(NavRoute.EnterDataOfFoundItemNavRoute.category).toString()*/

        EnterDataOfFoundItemScreen(
            navController = { navController.navigate(NavRoute.MainNavRoute.path) },
            /* name = name,
             description = description,
             color = color?: listOf<String>(),
             brand = brand,
             category = category*/
            viewModel = viewModel
        )
    }
}

fun openMapScreen(navController: NavHostController, navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(route = NavRoute.MapNavRoute.path) {
        MapScreen(
            navController = navController
        )
    }
}

fun openHomeScreen(navController: NavHostController, navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(route = NavRoute.HomeNavRoute.path) {
        HomeScreen(
            navController = navController
        )
    }
}

fun openMainScreen(navController: NavHostController, navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(route = NavRoute.MainNavRoute.path) {
        HomeScreen(
            navController = navController
        )
    }
}

fun openLostItemScreen(navController: NavHostController, navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(route = NavRoute.LostItemNavRoute.path) {
        LostItemScreen(
            navController = navController
        )
    }
}

fun openFoundItemScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    viewModel: FoundItemViewModel
) {
    navGraphBuilder.composable(route = NavRoute.FoundItemNavRoute.path) {
        FoundItemScreen(
            gotoLocationOfLostItems =
            { navController.navigate(NavRoute.LocationOfLostItemNavRoute.path) },
            viewModel
        )
    }
}

private fun openWelcomeScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = NavRoute.WelcomeNavRoute.path) { WelcomeScreen(navController = navController) }
}



