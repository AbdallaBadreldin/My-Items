package com.jetawy.data.repositories

import com.jetawy.data.firebase.FirebaseAuthService
import com.jetawy.data.firebase.FirebaseAuthServiceImpl
import com.jetawy.data.firebase.FirebaseDataBaseService
import com.jetawy.domain.repository.AccountSettingsRepository
import com.jetawy.domain.repository.FoundItemsRepository
import com.jetawy.domain.utils.AuthState
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AccountSettingsRepositoryImpl @Inject constructor(private val fbs: FirebaseAuthService) :
    AccountSettingsRepository {
    override suspend fun deleteAccount(): Flow<AuthState> {
        return fbs.deleteAccount()
    }
}