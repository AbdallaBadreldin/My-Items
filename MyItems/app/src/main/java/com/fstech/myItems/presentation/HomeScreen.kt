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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.fstech.myItems.R
import com.fstech.myItems.presentation.auth.AuthActivity
import com.fstech.myItems.presentation.found.FoundItemActivity
import com.fstech.myItems.presentation.lost.LostItemActivity
import com.fstech.myItems.presentation.matchmaking.MatchMakingActivity
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
                Text(text = stringResource(R.string.welcome_to_my_items_finder))
                Text(text = stringResource(R.string.pickup_a_service))
            }
        }

        Button(onClick = {
            if (Firebase.auth.currentUser == null)
                goToAuthentication(context)
            else
                goToFoundItemActivity(context)
        }) {
            Text(text = stringResource(R.string.i_found_an_item))
        }

        Button(onClick = {
            if (Firebase.auth.currentUser == null)
                goToAuthentication(context)
            else
                goToLostItemActivity(context)

        }) {
            Text(text = stringResource(R.string.i_lost_an_item))
        }

        Button(onClick = {
            if (Firebase.auth.currentUser == null)
                goToAuthentication(context)
            else
                goToMatchMakingActivity(context)

        }) {
            Text(text = stringResource(R.string.search_items_casually))
        }

    }
}

fun goToMatchMakingActivity(context: Context) {
    context.startActivity(Intent(context, MatchMakingActivity::class.java))

}

fun goToFoundItemActivity(context: Context) {
    context.startActivity(Intent(context, FoundItemActivity::class.java))
}

fun goToLostItemActivity(context: Context) {
    context.startActivity(Intent(context, LostItemActivity::class.java))
}

fun goToAuthentication(context: Context) {
    context.startActivity(Intent(context, AuthActivity::class.java))
}




