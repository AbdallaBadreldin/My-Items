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
    val colors: List<String?>?,
    val brand: String?,
    val category: String? = "",
    var userDescription: String? = "",
    var translatedDescription: String? = "",
    val model: String?="",
) : Parcelable