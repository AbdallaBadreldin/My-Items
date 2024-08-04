package com.jetawy.domain.repository

import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(
        message: String,
        sender: String,
        receiver: String,
        objectID: String
    ): Flow<UiState>

    suspend fun getChatRooms(): Flow<UiState>
    suspend fun getMessages(roomID: String): Flow<UiState>

}