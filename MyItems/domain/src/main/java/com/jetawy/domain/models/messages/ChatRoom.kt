package com.jetawy.domain.models.messages

import androidx.annotation.Keep

@Keep
data class ChatRoom(
    val roomID: String? = "",
    val sender: String? = "",
    val receiver: String? = "",
    val foundItemID: String? = "",
    var messages: List<MessageModel?>? = null, // Assuming 'MessageModel' is another data class
    var lastMessage: MessageModel? = null, // Assuming 'MessageModel' is another data class
    val timestamp: Long? = 0L,
    val isSeen: Boolean? = false,
    var lostItemId: String? = "",
    var itemType: String? = "",
    var isSenderBlocked: Boolean? = false,
    var isRecieverBlocked: Boolean? = false,
    var isDeleted: Boolean? = false,
)
