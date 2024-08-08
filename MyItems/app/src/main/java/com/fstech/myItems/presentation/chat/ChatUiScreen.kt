package com.fstech.myItems.presentation.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstech.myItems.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jetawy.domain.models.messages.MessageModel
import com.jetawy.domain.utils.UiState

@Composable
fun ChatUiScreen(viewModel: ChatViewModel) {
    val messagesStatus = viewModel.getMessages.collectAsState(initial = UiState.Loading)
    //Icon to delete found item from database
    when (messagesStatus.value) {
        is UiState.Error -> {
            val error = messagesStatus.value as UiState.Error
            Text(text = error.message)
            Button(onClick = { viewModel.getMessages() }) {
                Text(text = stringResource(id = R.string.retry))
            }
        }

        UiState.Initial -> {
            LaunchedEffect("dataFetchKey") {
                viewModel.getMessages()
            }
        }

        UiState.Loading -> {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.loading))
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(128.dp)
                )
            }
        }

        is UiState.Success<*> -> {
//            val chatRoom = viewModel.getChatRooms.collectAsState().value
            /*    val currentItemRoom = if (chatRoom is UiState.Success<*>) {
                    (chatRoom.outputData as ChatRoom)
                } else null
    */
            Column {
                if (Firebase.auth.currentUser?.uid == viewModel.currentItemRoom.receiver)
                    MyCardView(viewModel, viewModel.currentItemRoom.foundItemID)
                ChatScreen(
                    messages = (messagesStatus.value as UiState.Success<List<MessageModel>>).outputData,
                    onSendMessage = { message ->
                        viewModel.sendMessage(
                            message = message,
                            sender = Firebase.auth.currentUser?.uid.toString(),
                            receiver = viewModel.currentItemRoom.receiver.toString(),
                            roomID = viewModel.currentItemRoom.roomID.toString()
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ChatScreen(messages: List<MessageModel>, onSendMessage: (String) -> Unit) {
    var messageText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val listState = rememberLazyListState()
    var firstTime = true
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = false,
            state = listState
        ) {
            items(messages.size) { index ->
                val message = messages[index]
                MessageCard(message)
                LaunchedEffect(key1 = Unit) {
                    if (!listState.isScrollInProgress && firstTime) {
                        firstTime = false
                        listState.scrollToItem(listState.layoutInfo.totalItemsCount - 1)
                    }
                }
            }
        }

        LaunchedEffect("scroll") {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
        Row(modifier = Modifier.wrapContentSize()) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text(stringResource(R.string.enter_message)) }
            )
            IconButton(onClick = {
                if (messageText.isNotBlank()) {
                    onSendMessage(messageText)
                    messageText = ""
                }
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.send)
                )
            }
        }
    }
}


@Composable
fun MessageCard(message: MessageModel) {
    Column(
        horizontalAlignment = if (Firebase.auth.currentUser?.uid == message.sender) {
            Alignment.Start
        } else {
            Alignment.End
        },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        val sender =
            if (Firebase.auth.currentUser?.uid == message.sender) stringResource(id = R.string.you) else stringResource(
                id = R.string.other
            )
        Text(text = sender, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Card(modifier = Modifier.padding(8.dp)) {
            Text(
                text = message.message.toString(), style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun MyCardView(viewModel: ChatViewModel, foundItemID: String?) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.chat_options))
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.menu))
            }
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                onClick = { viewModel.deleteFoundItem(foundItemID) },
                text = { Text(stringResource(R.string.delete_this_item)) })
            DropdownMenuItem(
                onClick = { viewModel.deleteFoundItem(foundItemID) },
                text = { Text(stringResource(R.string.block_chat)) })
            DropdownMenuItem(
                onClick = { viewModel.deleteFoundItem(foundItemID) },
                text = { Text(stringResource(R.string.unblock_chat)) })

        }
    }
}