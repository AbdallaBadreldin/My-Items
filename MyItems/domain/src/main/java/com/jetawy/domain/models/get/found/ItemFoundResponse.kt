package com.jetawy.domain.models.get.found

import androidx.annotation.Keep

@Keep
data class ItemFoundResponse(
    var aiResponse: AiResponse? = AiResponse(),
    var location: String? = "",
    var objectID: String? = "",
    var timestamp: Long? = 0L,
    var user: String? = ""
)