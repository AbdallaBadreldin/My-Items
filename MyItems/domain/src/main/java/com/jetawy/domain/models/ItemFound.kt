package com.jetawy.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemFound(
    val type: String?,
    val nameLocalLanguage: String? ="",
    val description: String?="",
    val userDescription: String?="",
    val colors: List<String?>?,
    val brand: String?,
    val model: String?,
    val category: String?=""
) : Parcelable