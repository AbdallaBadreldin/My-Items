package com.jetawy.domain.repository

import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
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
        foundItemCountry: String,
        lostItemID: String,
        lostItemCountry: String,
        itemType: String,
    ): Flow<UiState>

    suspend fun getChatRooms(): Flow<UiState>
    suspend fun getMessages(roomID: String): Flow<UiState>

}