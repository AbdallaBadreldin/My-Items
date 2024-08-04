package com.fstech.myItems.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fstech.myItems.presentation.HomeScreen
import com.fstech.myItems.presentation.found.FoundItemEnterDataScreen
import com.fstech.myItems.presentation.found.FoundItemLocationScreen
import com.fstech.myItems.presentation.found.FoundItemScreen
import com.fstech.myItems.presentation.found.FoundItemUploadSuccessScreen
import com.fstech.myItems.presentation.found.FoundItemViewModel
import com.fstech.myItems.presentation.lost.LostItemEnterDataScreen
import com.fstech.myItems.presentation.lost.LostItemLocationScreen
import com.fstech.myItems.presentation.lost.LostItemScreen
import com.fstech.myItems.presentation.lost.LostItemUploadSuccessScreen
import com.fstech.myItems.presentation.lost.LostItemViewModel
import com.fstech.myItems.presentation.matchmaking.MatchDetailsScreen
import com.fstech.myItems.presentation.matchmaking.MatchMakingScreen
import com.fstech.myItems.presentation.matchmaking.MatchMakingSuccessScreen
import com.fstech.myItems.presentation.matchmaking.MatchMakingViewModel
import com.fstech.myItems.presentation.matchmaking.ShowItemsScreen
import com.fstech.myItems.presentation.welcome.WelcomeScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = NavRoute.WelcomeNavRoute.path) {
        openWelcomeScreen(navController = navController, this)
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
        openFoundItemLocationScreen(navController = navController, this, viewModel = viewModel)
        openFoundItemEnterDataScreen(navController = navController, this, viewModel)
        openFoundItemUploadSuccessScreen(navController = navController, this, viewModel)
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: LostItemViewModel
) {
    NavHost(navController = navController, startDestination = NavRoute.LostItemNavRoute.path) {
        openLostItemScreen(navController = navController, this, viewModel = viewModel)
        openLostItemLocationScreen(navController = navController, this, viewModel = viewModel)
        openLostItemEnterDataScreen(navController = navController, this, viewModel)
        openLostItemUploadSuccessScreen(navController = navController, this, viewModel)
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: MatchMakingViewModel
) {
    NavHost(navController = navController, startDestination = NavRoute.ShowItemsNavRoute.path) {
        openShowItemsScreen(navController = navController, this, viewModel)
        openMatchMakingScreen(navController = navController, this, viewModel)
        openMatchDetailsScreen(navController = navController, this, viewModel)
        openMatchMakingSuccessScreen(navController = navController, this, viewModel)
    }
}

fun openMatchMakingSuccessScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    viewModel: MatchMakingViewModel
) {
    navGraphBuilder.composable(
        route = NavRoute.MatchMakingSuccessNavRoute.path
    ) {
        MatchMakingSuccessScreen(viewModel)
    }
}

fun openMatchDetailsScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    viewModel: MatchMakingViewModel
) {
    navGraphBuilder.composable(
        route = NavRoute.MatchDetailsNavRoute.path
    ) {
        MatchDetailsScreen(
            { navController.navigate(NavRoute.MatchMakingSuccessNavRoute.path) },
            viewModel
        )
    }
}

fun openMatchMakingScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    viewModel: MatchMakingViewModel
) {
    navGraphBuilder.composable(
        route = NavRoute.MatchMakingNavRoute.path
    ) {
        MatchMakingScreen(
            goToMatchDetailsScreen = {
                navController.navigate(NavRoute.MatchDetailsNavRoute.path) {
                    popUpTo(NavRoute.MatchMakingNavRoute.path)
                }
            },
            viewModel
        )
    }
}

fun openShowItemsScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    viewModel: MatchMakingViewModel
) {
    navGraphBuilder.composable(
        route = NavRoute.ShowItemsNavRoute.path
    ) {
        ShowItemsScreen({ navController.navigate(NavRoute.MatchMakingNavRoute.path) }, viewModel)
    }
}

fun openLostItemEnterDataScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    viewModel: LostItemViewModel
) {
    navGraphBuilder.composable(
        route = NavRoute.LostItemEnterDataNavRoute.path
    ) {
        (LostItemEnterDataScreen(
            { navController.navigate(NavRoute.LostItemUploadSuccessNavRoute.path) },
            viewModel
        ))
    }
}

fun openLostItemUploadSuccessScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    viewModel: LostItemViewModel
) {
    navGraphBuilder.composable(
        route = NavRoute.LostItemUploadSuccessNavRoute.path
    ) {
        LostItemUploadSuccessScreen(viewModel)
    }
}

fun openLostItemLocationScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    viewModel: LostItemViewModel
) {
    navGraphBuilder.composable(
        route = NavRoute.LostItemLocationNavRoute.path
    ) {
        LostItemLocationScreen(
            { navController.navigate(NavRoute.LostItemEnterDataNavRoute.path) }, viewModel
        )
    }
}

fun openFoundItemUploadSuccessScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    viewModel: FoundItemViewModel
) {
    navGraphBuilder.composable(
        route = NavRoute.FoundItemUploadSuccessNavRoute.path
    ) {
        FoundItemUploadSuccessScreen(viewModel)
    }
}

fun openFoundItemLocationScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    viewModel: FoundItemViewModel
) {
    navGraphBuilder.composable(
        route = NavRoute.FoundItemLocationNavRoute.path
    ) {
        FoundItemLocationScreen(
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
        FoundItemEnterDataScreen(
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

fun openLostItemScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    viewModel: LostItemViewModel
) {
    navGraphBuilder.composable(route = NavRoute.LostItemNavRoute.path) {
        LostItemScreen(
            gotoLocationOfLostItems = { navController.navigate(NavRoute.LostItemLocationNavRoute.path) },
            viewModel = viewModel
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
            { navController.navigate(NavRoute.FoundItemLocationNavRoute.path) },
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



