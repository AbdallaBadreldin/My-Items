package com.fstech.myItems.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fstech.myItems.presentation.HomeScreen
import com.fstech.myItems.presentation.found.EnterDataOfFoundItemScreen
import com.fstech.myItems.presentation.found.FoundItemScreen
import com.fstech.myItems.presentation.found.FoundItemUploadSuccessScreen
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
        openFoundItemEnterDataScreen(navController = navController, this, viewModel)
        openFoundItemUploadSuccessScreen(navController = navController, this)
    }
}

fun openFoundItemUploadSuccessScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = NavRoute.FoundItemUploadSuccessNavRoute.path
    ) {
        FoundItemUploadSuccessScreen()
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
            navigateToEnterDataOfFoundItemScreen = { navController.navigate(NavRoute.FoundItemEnterDataNavRoute.path) },
            viewModel = viewModel
        )
    }
}

fun openFoundItemEnterDataScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    viewModel: FoundItemViewModel
) {
    navGraphBuilder.composable(
        route = NavRoute.FoundItemEnterDataNavRoute.path
    ) {
        EnterDataOfFoundItemScreen(
            goToFountItemSuccessScreen = {
                navController.navigate(NavRoute.FoundItemUploadSuccessNavRoute.path) {
//                    launchSingleTop = true
                }
            },
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



