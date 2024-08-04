package com.jetawy.domain.models.messages

data class ChatRoom(
    val roomID: String? = null,
    val sender: String? = null,
    val receiver: String? = null,
    val foundItemID: String? = null,
    val messages: List<MessageModel>? = null, // Assuming 'MessageModel' is another data class
    val timestamp: Long? = null,val isSeen: Boolean? = false
)
