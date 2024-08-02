package com.jetawy.domain.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ItemFound(
    val type: String?,
    val nameLocalLanguage: String? = "",
    val imageDescription: String? = "",
    var userDescription: String? = "",
    var translatedDescription: String? = "",
    val colors: List<String?>?,
    val brand: String?,
    val model: String?,
    val category: String? = ""
) : Parcelable