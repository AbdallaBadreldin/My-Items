package com.fstech.myItems.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetawy.domain.repository.AccountSettingsRepository
import com.jetawy.domain.utils.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repo: AccountSettingsRepository) :
    ViewModel() {
    private val _deleteAccount: MutableStateFlow<AuthState> =
        MutableStateFlow(AuthState.Initial)
    val deleteAccount: StateFlow<AuthState> =
        _deleteAccount.asStateFlow()


    fun deleteAccount() {
        viewModelScope.launch {
            repo.deleteAccount().collect {
                _deleteAccount.emit(it)
            }
        }
    }

}