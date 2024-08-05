package com.jetawy.data.firebase

import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow

interface FirebaseChatService {
    suspend fun sendMessage(
        message: String,
        sender: String,
        receiver: String,
        roomID: String
    ): Flow<UiState>
    suspend fun createChatRoom(
        message: String,
        sender: String,
        receiver: String,
        foundItemID: String,
        lostItemID: String,
    ): Flow<UiState>

    suspend fun getChatRooms(): Flow<UiState>
    suspend fun getMessages(roomID: String): Flow<UiState>

}