package com.jetawy.data.firebase

import com.jetawy.domain.utils.AuthState
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow

interface FirebaseService {
    fun signIn(phoneNumber: String,lang:String): Flow<AuthState>
    fun signOut()
    fun isLoggedIn(): Boolean
    fun verifyCode(code:String): Flow<AuthState>
}