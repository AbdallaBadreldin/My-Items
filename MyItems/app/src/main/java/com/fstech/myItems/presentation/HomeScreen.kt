package com.fstech.myItems.presentation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fstech.myItems.R
import com.fstech.myItems.presentation.auth.AuthActivity
import com.fstech.myItems.presentation.chat.ChatActivity
import com.fstech.myItems.presentation.found.FoundItemActivity
import com.fstech.myItems.presentation.lost.LostItemActivity
import com.fstech.myItems.presentation.matchmaking.MatchMakingActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    Column {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(64.dp)
                .clickable {
                    if (Firebase.auth.currentUser == null)
                        goToAuthentication(context)
                    else
                        goToChatActivity(context)
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_message_24),
                contentDescription = stringResource(
                    id = R.string.chat_icon
                )
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(64.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(16.dp)
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
}

fun goToChatActivity(context: Context) {
    context.startActivity(Intent(context, ChatActivity::class.java))
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




