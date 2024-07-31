package com.jetawy.data.repositories

import android.location.Address
import android.net.Uri
import com.jetawy.data.firebase.FirebaseDataBaseService
import com.jetawy.domain.models.ItemFound
import com.jetawy.domain.models.ItemLost
import com.jetawy.domain.repository.FoundItemsRepository
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FoundItemsRepositoryImpl @Inject constructor(private val fbs: FirebaseDataBaseService) :
    FoundItemsRepository {

    override suspend fun getFoundItemData(): Flow<UiState> {
        return fbs.getFoundItemData()
    }

    override suspend fun uploadFoundItems(
        imageUris: List<Uri>,
        addresses: Address,
        AiResponse: ItemFound,
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