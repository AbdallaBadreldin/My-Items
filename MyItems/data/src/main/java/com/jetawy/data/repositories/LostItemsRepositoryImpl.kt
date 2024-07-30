package com.jetawy.data.repositories

import android.location.Address
import android.net.Uri
import com.jetawy.data.firebase.FirebaseDataBaseService
import com.jetawy.domain.models.ItemResponse
import com.jetawy.domain.repository.LostItemsRepository
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LostItemsRepositoryImpl @Inject constructor(private val fbs: FirebaseDataBaseService) :
    LostItemsRepository {

    override suspend fun getLostItemData(): Flow<UiState> {
        return fbs.getFoundItemData()
    }

    override suspend fun uploadLostItems(
        imageUris: List<Uri>,
        addresses: Address,
        AiResponse: ItemResponse,
        userDescription: String
    ): Flow<UiState> {
        return fbs.uploadFoundItems(
            imageUris,
            addresses,
            AiResponse,
            userDescription
        )
    }


}