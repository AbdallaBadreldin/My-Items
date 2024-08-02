package com.jetawy.domain.models.get.lost

import androidx.annotation.Keep

@Keep
data class UserResponse(
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