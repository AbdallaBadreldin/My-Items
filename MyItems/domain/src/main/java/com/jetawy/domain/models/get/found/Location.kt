package com.jetawy.domain.models.get.found

import androidx.annotation.Keep

@Keep
data class Location(
    var latitude: Double? = null,
    var longitude: Double? = null
)