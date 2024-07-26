package com.fstech.myItems.presentation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.fstech.myItems.navigation.Screen
import com.fstech.myItems.presentation.auth.AuthActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Welcome To My Items Finder")
                Text(text = "Pickup a Service")
            }
        }

        Button(onClick = {
            if (Firebase.auth.currentUser == null)
                goToAuthentication(context)
            else
                navController.navigate(Screen.FoundItemScreen.route)
        }) {
            Text(text = "I Found An Item")
        }

        Button(onClick = {
            if (Firebase.auth.currentUser == null)
                goToAuthentication(context)
            else   navController.navigate(Screen.LostItemScreen.route)
        }) {
            Text(text = "I Lost An Item")
        }

        Button(onClick = {
            if (Firebase.auth.currentUser == null)
                goToAuthentication(context)
            else   navController.navigate(Screen.MapScreen.route)
        }) {
            Text(text = "Search Items Casually")
        }

    }
}
fun goToAuthentication(context: Context) {
    context.startActivity(Intent(context, AuthActivity::class.java))
}




