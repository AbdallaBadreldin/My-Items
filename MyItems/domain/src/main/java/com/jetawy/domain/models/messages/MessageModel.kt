package com.jetawy.domain.models.messages

import androidx.annotation.Keep

@Keep
data class MessageModel (
    var message: String? = null,
    var sender: String? = null,
    var receiver: String? = null,
    var isSeen:Boolean = false,
    var timestamp: Long? = System.currentTimeMillis(),
)