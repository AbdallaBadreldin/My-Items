package com.jetawy.domain.models.get.lost

import androidx.annotation.Keep

@Keep
data class ItemLostResponse(
    var aiResponse: AiResponse? = AiResponse(),
    var location: String? = "",
    var objectID: String? = "",
    var timestamp: Long? = 0L,
    var user: String? = "",
    var userResponse: UserResponse? = UserResponse()
)