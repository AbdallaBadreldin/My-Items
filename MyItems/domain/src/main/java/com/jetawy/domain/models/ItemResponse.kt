package com.jetawy.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemResponse(
    val name: String,
    val description: String,
    val color: List<String>,
    val brand: String,
    val category: String
) : Parcelable