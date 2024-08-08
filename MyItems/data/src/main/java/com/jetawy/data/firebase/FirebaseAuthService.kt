package com.jetawy.data.firebase

import com.jetawy.domain.utils.AuthState
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthService {
    fun signIn(phoneNumber: String,lang:String): Flow<AuthState>
    fun signOut()
    fun isLoggedIn(): Boolean
    fun verifyCode(code:String): Flow<AuthState>
    suspend fun deleteAccount(): Flow<AuthState>
}