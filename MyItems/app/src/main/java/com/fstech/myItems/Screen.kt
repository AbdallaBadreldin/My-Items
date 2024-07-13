package com.fstech.myItems

sealed class Screen(val route :String) {
object WelcomeScreen : Screen("welcome_screen")
object MapScreen : Screen("map_screen")
object MainScreen : Screen("main_screen")
object DetailsScreen : Screen("details_screen")
}