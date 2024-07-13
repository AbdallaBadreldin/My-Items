package com.fstech.myItems

sealed class Screen(val route :String) {
object MainScreen : Screen("main_screen")
object DetailsScreen : Screen("details_screen")
}