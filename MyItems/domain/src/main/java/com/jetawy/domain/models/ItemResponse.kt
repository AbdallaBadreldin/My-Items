package com.jetawy.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.intellij.lang.annotations.Language

@Parcelize
data class ItemResponse(
    val name: String,
    val nameLocalLanguage: String,
    val description: String,
    val color: List<String>,
    val brand: String,
    val category: String
) : Parcelable