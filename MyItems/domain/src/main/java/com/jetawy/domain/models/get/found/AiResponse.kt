package com.jetawy.domain.models.get.found

import androidx.annotation.Keep

@Keep
data class AiResponse(
    var brand: String? = "",
    var category: String? = "",
    var colors: List<String?>? = listOf(),
    var imageDescription: String? = "",
    var itemState: String? = "",
    var model: String? = "",
    var translatedDescription: String? = "",
    var type: String? = "",
    var userDescription: String? = ""
)