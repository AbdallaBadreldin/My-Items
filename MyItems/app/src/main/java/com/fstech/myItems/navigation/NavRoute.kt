package com.fstech.myItems.navigation

sealed class NavRoute(val path: String) {

    object WelcomeNavRoute : NavRoute("welcome_screen")
    object HomeNavRoute : NavRoute("home_screen")
    object MapNavRoute : NavRoute("map_screen")
    object MainNavRoute : NavRoute("main_screen")
    object BakingNavRoute : NavRoute("backing_screen")
    object FoundItemNavRoute : NavRoute("found_item_screen")
    object LostItemNavRoute : NavRoute("lost_item_screen")
    object FoundItemUploadSuccessNavRoute : NavRoute("found_item_upload_success_screen")
    object FoundItemEnterDataNavRoute : NavRoute("enter_data_of_found_item_screen")
    object FoundItemLocationNavRoute : NavRoute("location_of_found_item_screen")
    object LostItemLocationNavRoute : NavRoute("lost_item_location_screen")
    object LostItemEnterDataNavRoute : NavRoute("lost_item_enter_data_screen")
    object LostItemUploadSuccessNavRoute : NavRoute("lost_item_upload_success_screen")

   //matchmaking
    object ShowItemsNavRoute : NavRoute("show_items_screen")
    object MatchMakingNavRoute : NavRoute("matchmaking_screen")

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
