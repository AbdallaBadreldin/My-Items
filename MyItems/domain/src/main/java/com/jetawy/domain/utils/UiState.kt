package com.jetawy.domain.utils

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface UiState {

    /**
     * Empty state when the screen is first shown
     */
    object Initial : UiState

    /**
     * Still loading
     */
    object Loading : UiState

    /**
     * Text has been generated
     */
    data class Success<out T>(val outputData: T) : UiState

    /**
     * There was an error generating text
     */
    data class Error(val message: String) : UiState
}