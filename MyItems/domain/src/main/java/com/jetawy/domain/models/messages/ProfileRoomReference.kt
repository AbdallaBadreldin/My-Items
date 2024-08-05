package com.jetawy.domain.models.messages

import androidx.annotation.Keep

@Keep
data class ProfileRoomReference(
    var isSeen: Boolean? = false,
    var roomID: String? = "",
    var timestamp: Long? = 0L
)