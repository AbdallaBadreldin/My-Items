package com.fstech.myItems

sealed class Screen(val route :String) {
object WelcomeScreen : Screen("welcome_screen")
object HomeScreen : Screen("home_screen")
object MapScreen : Screen("map_screen")
object MainScreen : Screen("main_screen")
object BakingScreen : Screen("backing_screen")
object FoundItemScreen : Screen("found_item_screen")
object LostItemScreen : Screen("lost_item_screen")
}