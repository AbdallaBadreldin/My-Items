package com.jetawy.domain.utils

import java.lang.Exception


sealed interface AuthState {

    /**
     * Empty state when the screen is first shown
     */
    object Initial : AuthState

    /**
     * Still loading
     */
    object Loading : AuthState

    /**
     * Still loading
     */
    object OnCodeSent : AuthState

    /**
     * Text has been generated
     */
    object OnSuccess : AuthState

    /**
     * There was an error generating text
     */
    data class Error(val error: Exception?) : AuthState
}