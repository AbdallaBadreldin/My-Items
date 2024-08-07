package com.fstech.myItems.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetawy.domain.models.messages.ChatRoom
import com.jetawy.domain.repository.ChatRepository
import com.jetawy.domain.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repo: ChatRepository) : ViewModel() {
    lateinit var currentItemRoom: ChatRoom

    private val _getChatRooms: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    var getChatRooms: StateFlow<UiState> =
        _getChatRooms.asStateFlow()

    fun getChatRooms() {
        viewModelScope.launch {
            repo.getChatRooms().collect { req ->
                _getChatRooms.emit(req)
            }
        }
    }

    fun sendMessage(message: String, sender: String, receiver: String, roomID: String) {
        viewModelScope.launch {
            repo.sendMessage(message, sender, receiver, roomID)
        }
    }


    private val _closeActivity = MutableSharedFlow<Unit>()
    val closeActivity = _closeActivity.asSharedFlow()


    fun triggerCloseActivity() {
        viewModelScope.launch {
            _closeActivity.emit(Unit)
        }
    }

    private val _getMessages: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    var getMessages: StateFlow<UiState> =
        _getMessages.asStateFlow()

    fun getMessages() {
        viewModelScope.launch {
            repo.getMessages(currentItemRoom.roomID.toString()).collect { req ->
                _getMessages.emit(req)
            }
        }
    }

    fun resetMessagesList() {
        viewModelScope.launch {
            _getMessages.emit(UiState.Initial)
        }
    }
}