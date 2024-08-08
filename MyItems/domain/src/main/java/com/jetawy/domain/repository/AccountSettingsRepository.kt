package com.jetawy.domain.repository

import com.jetawy.domain.utils.AuthState
import kotlinx.coroutines.flow.Flow

interface AccountSettingsRepository {
    suspend fun deleteAccount(): Flow<AuthState>
}