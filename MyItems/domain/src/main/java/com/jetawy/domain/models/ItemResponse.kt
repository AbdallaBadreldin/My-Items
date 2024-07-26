package com.jetawy.domain.models

data class ItemResponse(
    val name: String? = null,
    val description: String? = null,
    val color: List<String>? = null,
    val brand: String? = null,
    val category: String? = null
)