package com.fstech.myItems.presentation.matchmaking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetawy.data.repositories.FoundItemsRepositoryImpl
import com.jetawy.data.repositories.LostItemsRepositoryImpl
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
class MatchMakingViewModel @Inject constructor(
    private val foundItemsRepo: FoundItemsRepositoryImpl,
    private val lostItemsRepo: LostItemsRepositoryImpl
) : ViewModel() {
    var itemIndex = 0

    private val _foundUiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val foundUiState: StateFlow<UiState> =
        _foundUiState.asStateFlow()

    private val _lostUiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val lostUiState: StateFlow<UiState> =
        _lostUiState.asStateFlow()

    private val _getFoundUiStateByCountry: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val getFoundUiStateByCountry: StateFlow<UiState> =
        _getFoundUiStateByCountry.asStateFlow()

    fun getFoundItemByCountry(country: String) {
        viewModelScope.launch {
            foundItemsRepo.getFoundItemsByCountry(country).collect {
                _getFoundUiStateByCountry.emit(it)
            }
        }
    }

    fun getFoundItemById() {
        viewModelScope.launch {
            foundItemsRepo.getFoundItemsById().collect {
                _foundUiState.emit(it)
            }
        }
    }

    fun getLostItemById() {
        viewModelScope.launch {
            lostItemsRepo.getLostItemById().collect {
                _lostUiState.emit(it)
            }
        }
    }

    private val _closeActivity = MutableSharedFlow<Unit>()
    val closeActivity = _closeActivity.asSharedFlow()

    fun triggerCloseActivity() {
        viewModelScope.launch {
            _closeActivity.emit(Unit)
        }
    }

}