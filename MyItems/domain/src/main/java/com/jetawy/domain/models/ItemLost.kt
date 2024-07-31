package com.jetawy.domain.models

data class ItemLost(
    val type: String?=null,
    val model: String?=null,
    val brand: String?=null,
    val category: List<String>?=null,
    val itemState: String?=null,
    val colors: List<String>?=null,
    val description: String?=null
)

