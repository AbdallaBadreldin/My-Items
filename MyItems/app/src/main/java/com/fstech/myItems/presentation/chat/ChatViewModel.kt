package com.fstech.myItems.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor():ViewModel() {
    private val _closeActivity = MutableSharedFlow<Unit>()
    val closeActivity = _closeActivity.asSharedFlow()

    fun triggerCloseActivity() {
        viewModelScope.launch {
            _closeActivity.emit(Unit)
        }
    }
}