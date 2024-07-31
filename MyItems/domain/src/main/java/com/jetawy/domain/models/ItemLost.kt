package com.jetawy.domain.models

data class ItemLost(
    val name: String,
    val model: String,
    val brand: String,
    val category: List<String>,
    val itemState: String,
    val colors: List<String>,
    val description: String
)

