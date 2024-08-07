package com.jetawy.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jetawy.domain.models.messages.ChatRoom
import com.jetawy.domain.models.messages.MessageModel
import com.jetawy.domain.models.messages.ProfileRoomReference
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
        val ref = db.getReference("/messages/$roomID").push()

        val updateRoomLstSender = db.getReference("/chatRooms/$roomID/lastMessage")

        try {
            updateRoomLstSender.setValue(messageModel)
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
        foundItemCountry: String,
        lostItemID: String,
        lostItemCountry: String,
        itemType: String,
    ): Flow<UiState> {
        _createChatRoom.emit(UiState.Loading)
        try {
            val ref = db.getReference("chatRooms").push()

            ref.child("roomID").setValue(ref.key)
            ref.child("sender").setValue(sender)
            ref.child("receiver").setValue(receiver)
            ref.child("foundItemID").setValue(foundItemID)
            ref.child("lostItemId").setValue(lostItemID)
            ref.child("itemType").setValue(itemType)
            ref.child("timestamp").setValue(System.currentTimeMillis())
//            ref.child("isSeen").setValue(false)
            //room created successfully

            val senderProfileRef = db.getReference("profiles/$sender/chatRooms/${ref.key}")
            val receiverProfileRef = db.getReference("profiles/$receiver/chatRooms/${ref.key}")

            senderProfileRef.child("roomID").setValue(ref.key)
            senderProfileRef.child("timestamp").setValue(System.currentTimeMillis())
            senderProfileRef.child("isSeen").setValue(true)

            receiverProfileRef.child("roomID").setValue(ref.key)
            receiverProfileRef.child("timestamp").setValue(System.currentTimeMillis())
            receiverProfileRef.child("isSeen").setValue(false)
            // notified users that chat room is created successfully

            val itemRef = db.getReference("/lostItems/$lostItemCountry/$lostItemID")
            val itemData = itemRef.get().await()
            val destinationItemRef = db.getReference("/doneLostItems/$lostItemCountry/$lostItemID")
            destinationItemRef.setValue(itemData.value)
            itemRef.removeValue()
            //deleted the object from lost items from database successfully

            val profileRef =
                db.getReference("/profiles/$sender/lostItems/$lostItemID")
            val profileData = profileRef.get().await()
            val destinationProfileRef =
                db.getReference("/profiles/$sender/doneLostItems/$lostItemID")
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
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val ref =
                    db.getReference("/profiles/${FirebaseAuth.getInstance().currentUser?.uid}/chatRooms")

                ref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        runBlocking {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            val roomsDetails = mutableListOf<ChatRoom?>()
                            val myList =
                                dataSnapshot.children.mapNotNull { it.getValue(ProfileRoomReference::class.java) }

                            myList.forEach {
                                val detailsRef = db.getReference("/chatRooms/${it.roomID}")
                                val roomDetails = detailsRef.get().await()

                                val messagesRef = db.getReference("/messages/${it.roomID}")
                                val messagesList = messagesRef.get().await()

                                val messages =
                                    messagesList.children.mapNotNull { it.getValue(MessageModel::class.java) }
                                roomsDetails.add(roomDetails.getValue(ChatRoom::class.java))
                                roomsDetails[roomsDetails.size - 1]?.messages = messages
                            }
                            // Process the retrieved data (value) here
                            Log.e("FirebaseData", "Value is: $roomsDetails")
                            _getChatRooms.emit(UiState.Success(roomsDetails))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        runBlocking {
                            _getChatRooms.emit(UiState.Error(error.message))
                        }
                    }
                })
            } catch (e: Exception) {
                _getChatRooms.emit(UiState.Error(e.message.toString()))
            }
        }
        return getChatRooms
    }

    private val _getMessages: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val getMessages: Flow<UiState> =
        _getMessages.asStateFlow()

    override suspend fun getMessages(roomID: String): Flow<UiState> {
        _getMessages.emit(UiState.Loading)

        try {
            val ref = db.getReference("/messages/$roomID")
            ref.addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    runBlocking {
                        val messages = mutableListOf<MessageModel?>()
                        val myList =
                            dataSnapshot.children.mapNotNull { it.getValue(MessageModel::class.java) }
                        messages.addAll(myList)
                        _getMessages.emit(UiState.Success(messages))
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
            )
        } catch (e: Exception) {
            _getMessages.emit(UiState.Error(e.message.toString()))
        }
        return getMessages
    }


}