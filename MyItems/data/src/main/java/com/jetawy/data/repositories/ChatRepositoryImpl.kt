package com.jetawy.data.repositories

import com.jetawy.data.firebase.FirebaseChatService
import com.jetawy.domain.repository.ChatRepository
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(private val fbs: FirebaseChatService) :
    ChatRepository {
    override suspend fun sendMessage(
        message: String,
        sender: String,
        receiver: String,
        roomID: String
    ): Flow<UiState> {
        return fbs.sendMessage(message, sender, receiver, roomID)
    }

    override suspend fun createChatRoom(
        message: String,
        sender: String,
        receiver: String,
        foundItemID: String,
        foundItemCountry: String,
        lostItemID: String,
        lostItemCountry: String,
        itemType: String,
    ): Flow<UiState> {
        return fbs.createChatRoom(
            message,
            sender,
            receiver,
            foundItemID,
            foundItemCountry,
            lostItemID,
            lostItemCountry,
            itemType
        )
    }

    override suspend fun getChatRooms(): Flow<UiState> {
        return fbs.getChatRooms()
    }

    override suspend fun getMessages(roomID: String): Flow<UiState> {
        return fbs.getMessages(roomID)
    }
}