package com.jetawy.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jetawy.domain.models.messages.MessageModel
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebaseChatServiceImpl(private val db: FirebaseDatabase) : FirebaseChatService {


    private val _sendMessage: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val sendMessage: Flow<UiState> =
        _sendMessage.asStateFlow()

    override suspend fun sendMessage(
        message: String,
        sender: String,
        receiver: String,
        roomID: String
    ): Flow<UiState> {
        _sendMessage.emit(UiState.Loading)

        val messageModel = MessageModel(message, sender, receiver)
        val ref = db.getReference("chatRooms/$roomID/messages").push()

        try {
            ref.setValue(messageModel)
            _sendMessage.emit(UiState.Success(ref.key))
        } catch (e: Exception) {
            _sendMessage.emit(UiState.Error(e.message.toString()))
        }
        return sendMessage
    }

    private val _createChatRoom: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val createChatRoom: Flow<UiState> =
        _createChatRoom.asStateFlow()

    override suspend fun createChatRoom(
        message: String,
        sender: String,
        receiver: String,
        foundItemID: String,
        lostItemID: String,
    ): Flow<UiState> {
        _createChatRoom.emit(UiState.Loading)
        try {
            val ref = db.getReference("chatRooms").push()
            val messageModel = MessageModel(message, sender, receiver)

            ref.child("roomID").setValue(ref.key)
            ref.child("sender").setValue(sender)
            ref.child("receiver").setValue(receiver)
            ref.child("foundItemID").setValue(foundItemID)
            ref.child("lostItemId").setValue(lostItemID)
            ref.child("messages").push().setValue(messageModel)
            ref.child("timestamp").setValue(System.currentTimeMillis())
            ref.child("isSeen").setValue(false)
            //room created successfully

            val senderProfileRef = db.getReference("profiles/$sender/chatRooms").push()
            val receiverProfileRef = db.getReference("profiles/$receiver/chatRooms").push()

            senderProfileRef.child("roomID").setValue(ref.key)
            senderProfileRef.child("timestamp").setValue(System.currentTimeMillis())
            senderProfileRef.child("isSeen").setValue(true)

            receiverProfileRef.child("roomID").setValue(ref.key)
            receiverProfileRef.child("timestamp").setValue(System.currentTimeMillis())
            receiverProfileRef.child("isSeen").setValue(false)
            // notified users that chat room is created successfully

            val itemRef = db.getReference("lostItems/$lostItemID")
            val itemData = itemRef.get().await()
            val destinationItemRef = db.getReference("doneLostItems/$lostItemID")
            destinationItemRef.setValue(itemData.value)
            itemRef.removeValue()
            //deleted the object from lost items from database successfully

            val profileRef =
                db.getReference("profiles/$sender/lostItems/$lostItemID")
            val profileData = profileRef.get().await()
            val destinationProfileRef =
                db.getReference("profiles/$sender/doneLostItems/$lostItemID")
            destinationProfileRef.setValue(profileData.value)
            profileRef.removeValue()
            //deleted the object from lost items from profile and database successfully

            sendMessage(
                message = message,
                sender = sender,
                receiver = receiver,
                roomID = ref.key.toString()
            )
            //sent message to room successfully


            //room created successfully and notified users and deleted object from history and database after that sent message to room
            _createChatRoom.emit(UiState.Success(ref.key))
        } catch (e: Exception) {
            _createChatRoom.emit(UiState.Error(e.message.toString()))
        }
        return createChatRoom
    }

    private val _getChatRooms: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val getChatRooms: Flow<UiState> =
        _getChatRooms.asStateFlow()

    override suspend fun getChatRooms(): Flow<UiState> {
        _getChatRooms.emit(UiState.Loading)
        try {
            val ref = db.getReference("/${FirebaseAuth.getInstance().currentUser?.uid}/chatRooms")
            val refData = ref.get().await()
            val rooms = mutableListOf<MessageModel>()
            refData.children.forEach {
                rooms.add(it.getValue(MessageModel::class.java)!!)
            }
            _getChatRooms.emit(UiState.Success(rooms))
        } catch (e: Exception) {
            _getChatRooms.emit(UiState.Error(e.message.toString()))
        }
        return getChatRooms
    }

    private val _getMessages: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val getMessages: Flow<UiState> =
        _getMessages.asStateFlow()

    override suspend fun getMessages(roomID: String): Flow<UiState> {
        TODO("Not yet implemented")
    }
}