package com.fstech.myItems.presentation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.fstech.myItems.R
import com.fstech.myItems.presentation.auth.AuthActivity
import com.fstech.myItems.presentation.chat.ChatActivity
import com.fstech.myItems.presentation.found.FoundItemActivity
import com.fstech.myItems.presentation.lost.LostItemActivity
import com.fstech.myItems.presentation.matchmaking.MatchMakingActivity
import com.fstech.myItems.presentation.settings.SettingsActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle
    var showMenu by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(Firebase.auth.currentUser != null) }
    LaunchedEffect(lifecycleOwner) {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                showSettings = Firebase.auth.currentUser != null
            }

            override fun onPause(owner: LifecycleOwner) {
                showSettings = Firebase.auth.currentUser != null
            }
        })
    }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, top = 64.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showSettings)
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.menu),
                    )
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                goToSettings(context)
                                showMenu = !showMenu
                            },
                            text = { Text(stringResource(R.string.settings)) })
                    }
                }
            Box(
                modifier = Modifier
                    .wrapContentSize()
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

fun goToSettings(context: Context) {
    context.startActivity(Intent(context, SettingsActivity::class.java))
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




