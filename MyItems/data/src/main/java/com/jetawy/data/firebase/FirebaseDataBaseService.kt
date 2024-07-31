package com.jetawy.data.firebase

import android.location.Address
import android.net.Uri
import com.jetawy.domain.models.ItemLost
import com.jetawy.domain.models.ItemFound
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow

interface FirebaseDataBaseService {

    suspend fun getLostItemData(): Flow<UiState>
    suspend fun uploadLostItems(
        imageUris: List<Uri>,
        addresses: Address,
        aiResponse: ItemLost,
        userDescription: String
    ): Flow<UiState>

    suspend fun getFoundItemData(): Flow<UiState>
    suspend fun uploadFoundItems(
        imageUris: List<Uri>,
        addresses: Address,
        aiResponse: ItemFound,
        userDescription: String
    ): Flow<UiState>
}
