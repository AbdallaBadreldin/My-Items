package com.jetawy.domain.models

data class ItemLost(
    val type: String?="",
    val model: String?="",
    val brand: String?="",
    val category: String?="",
    val itemState: String?="",
    val colors: List<String>?= listOf(),
    val imageDescription: String?="",
    var userDescription: String?="",
    val translatedDescription: String?=""
)

