package com.fstech.myItems.navigation

sealed class NavRoute(val path: String) {

    object WelcomeNavRoute : NavRoute("welcome_screen")
    object HomeNavRoute : NavRoute("home_screen")
    object MapNavRoute : NavRoute("map_screen")
    object MainNavRoute : NavRoute("main_screen")
    object BakingNavRoute : NavRoute("backing_screen")
    object FoundItemNavRoute : NavRoute("found_item_screen")
    object LostItemNavRoute : NavRoute("lost_item_screen")
    object EnterDataOfFoundItemNavRoute : NavRoute("enter_data_of_found_item_screen") {
        /* val name= "name"
         val description= "description"
         val color= "color"
         val brand= "brand"
         val category= "category"*/
    }

    object LocationOfLostItemNavRoute : NavRoute("location_of_lost_item_screen")

    // build navigation path (for screen navigation)
    fun withArgs(vararg args: String): String {
        return buildString {
            append(path)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

    // build and setup route format (in navigation graph)
    fun withArgsFormat(vararg args: String): String {
        return buildString {
            append(path)
            args.forEach { arg ->
                append("/{$arg}")
            }
        }
    }
}
