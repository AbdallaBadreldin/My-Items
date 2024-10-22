package com.fstech.myItems.presentation.chat

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstech.myItems.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jetawy.domain.models.messages.ChatRoom
import com.jetawy.domain.utils.UiState
import com.jetawy.domain.utils.Utils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatListScreen(goToChatUiScreen: () -> Unit, viewModel: ChatViewModel) {
    val context = LocalContext.current
    val chatRooms = viewModel.getChatRooms.collectAsState()

    when (chatRooms.value) {
        is UiState.Error -> {
            Column {
                val error = chatRooms.value as UiState.Error
                Text(
                    text = error.message, modifier = Modifier
                        .padding(16.dp)
                        .wrapContentSize()
                )
                Button(
                    onClick = {
                        startGettingData(viewModel, context)
                    }, modifier = Modifier
                        .padding(16.dp)
                        .wrapContentSize()
                ) {
                    Text(
                        text = stringResource(id = R.string.retry), modifier = Modifier
                            .padding(16.dp)
                            .wrapContentSize()
                    )
                }
            }
        }

        UiState.Initial -> {
            startGettingData(viewModel, context)
        }

        UiState.Loading -> {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.loading),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                )
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(128.dp)
                )
            }
        }

        is UiState.Success<*> -> {
            val response = chatRooms.value as UiState.Success<*>
            val data = response.outputData as MutableList<ChatRoom>
            LazyColumn {
                items(data.size) { index ->
                    ChatRoomItem(data[index], viewModel, goToChatUiScreen)
                }
            }
        }
    }
}

@Composable
fun ChatRoomItem(chatRoom: ChatRoom, viewModel: ChatViewModel, goToChatUiScreen: () -> Unit) {
    if (chatRoom.sender == null) {
//the room is deleted!!!
    }
    Column(
        modifier = Modifier
            .padding(32.dp)
            .fillMaxWidth()
            .border(1.dp, androidx.compose.ui.graphics.Color.Red)
            .clickable {
                viewModel.currentItemRoom = chatRoom
                viewModel.resetMessagesList()
                goToChatUiScreen()
            //we need to go to chat screen
            }
    ) {
        Text(
            text = chatRoom.itemType.toString(),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (chatRoom.sender.toString() == Firebase.auth.currentUser?.uid) {
            Text(text = stringResource(R.string.your_lost_item), Modifier.padding(8.dp))
        } else {
            Text(text = stringResource(R.string.someone_lost_item), Modifier.padding(8.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            if (chatRoom.lastMessage?.sender.toString() == Firebase.auth.currentUser?.uid) {
                Text(text = stringResource(R.string.message_sent), Modifier.padding(8.dp))
            } else {
                Text(text = stringResource(R.string.message_received), Modifier.padding(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = convertTimestampToDate(chatRoom.lastMessage?.timestamp ?: 0L),
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

fun convertTimestampToDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(date)
}

fun startGettingData(viewModel: ChatViewModel, context: Context) {
    if (Utils.isInternetAvailable(context = context)) {
        viewModel.getChatRooms()
    } else {
        viewModel.noInternetConnection(context.getString(R.string.no_internet_connection))
    }
}
