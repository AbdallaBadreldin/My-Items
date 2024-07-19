package com.jetawy.domain.utils

sealed class RequestStatus<out T> {
//    data object Waiting : RequestStatus<Nothing>()
    object init : RequestStatus<Nothing>()
    object Waiting : RequestStatus<Nothing>()
    data class Success<out T>(val data: T) : RequestStatus<T>()
    data class Error(val message: String) : RequestStatus<Nothing>()
}

