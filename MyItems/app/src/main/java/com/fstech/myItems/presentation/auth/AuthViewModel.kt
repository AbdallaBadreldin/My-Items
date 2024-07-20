package com.fstech.myItems.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetawy.data.firebase.FirebaseService
import com.jetawy.domain.utils.AuthState
import com.jetawy.domain.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "AuthViewModel"

@HiltViewModel
class AuthViewModel @Inject constructor(private val firebase: FirebaseService) : ViewModel() {
    private val _codeSent = MutableLiveData<AuthState>()
    val codeSent: LiveData<AuthState> get() = _codeSent

    fun signIn(phoneNumber: String, language: String) {
        viewModelScope.launch {
            firebase.signIn(phoneNumber, language).collect { req ->
                _codeSent.postValue(req)
            }
        }
    }

    fun verifyCode(code: String) {
        viewModelScope.launch {
            firebase.verifyCode(code).collect { req ->
                _codeSent.postValue(req)
            }
        }
    }

    fun isLoggedIn() = firebase.isLoggedIn()

    fun signOut() = firebase.signOut()

}