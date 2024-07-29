package com.jetawy.data.firebase

import android.location.Address
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.jetawy.domain.models.ItemResponse
import com.jetawy.domain.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDataBaseServiceImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val db: FirebaseDatabase
) : FirebaseDataBaseService {
    private val _uploadFoundItem: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uploadFoundItem: Flow<UiState> =
        _uploadFoundItem.asStateFlow()

    private val _getFoundItemData: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val getFoundItemData: Flow<UiState> =
        _getFoundItemData.asStateFlow()

    private val _uploadLostItem: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uploadLostItem: Flow<UiState> =
        _uploadLostItem.asStateFlow()

    private val _getLostItemData: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val getLostItemData: Flow<UiState> =
        _getLostItemData.asStateFlow()

    override suspend fun uploadFoundItems(
        imageUris: List<Uri>,
        addresses: Address,
        AiResponse: ItemResponse,
        userDescription: String
    ): Flow<UiState> {
        //first we will upload photos to firebase storage
        _uploadFoundItem.emit(UiState.Loading)
        val listOfDownloadUrls = mutableListOf<String>()
        imageUris.forEach { uri ->
            val fileName = uri.lastPathSegment ?: "image.jpg" // Get file name or use a default
            val imageRef =
                storage.reference.child("FoundItemsImages/$fileName")// Create a reference to the image in Firebase Storage
            try {
                imageRef.putFile(uri).await() // Upload the image
                val downloadUrl = imageRef.downloadUrl.await() // Get the download URL
                Log.d("FirebaseStorage", "Upload successful. Download URL: $downloadUrl")
                Log.d("FirebaseStorage", "Upload successful. Download URL: ${downloadUrl.path}")
                listOfDownloadUrls.add(downloadUrl.toString())
                // Store the download URL in your database or use it as needed
            } catch (e: Exception) {
                // Handle upload errors
                _uploadFoundItem.emit(UiState.Error(e.message.toString()))
                return uploadFoundItem
            }
        }
        //second we will upload data to firebase database
        val myRef = db.getReference("foundItems/${addresses.countryName}").push()
        try {
            myRef.child("images").setValue(listOfDownloadUrls).await()
            myRef.child("location").setValue("${addresses.latitude},${addresses.longitude}").await()
            myRef.child("addressUrl").setValue(addresses.url).await()
            myRef.setValue(AiResponse).await()
            myRef.child("userDescription").setValue(userDescription).await()
            myRef.child("user").setValue(FirebaseAuth.getInstance().currentUser?.uid).await()
            myRef.child("timestamp").setValue(System.currentTimeMillis()).await()
        } catch (e: Exception) {
            _uploadFoundItem.emit(UiState.Error(e.message.toString()))
            return uploadFoundItem
        }

        //third we will upload reference to uploaded data in user profile
        val myProfileRef =
            db.getReference("profiles/${FirebaseAuth.getInstance().currentUser?.uid}/foundItems")
                .push()
        try {
            myProfileRef.setValue(myRef).await()
            _uploadFoundItem.emit(UiState.Success(myProfileRef.toString()))
        } catch (e: Exception) {
            _uploadFoundItem.emit(UiState.Error(e.message.toString()))
            return uploadFoundItem
        }
        return uploadFoundItem
    }

    override suspend fun getFoundItemData(): Flow<UiState> {
        _getFoundItemData.emit(UiState.Loading)
        // TODO implement this function
        return getFoundItemData
    }

    override suspend fun uploadLostItems(
        imageUris: List<Uri>,
        addresses: Address,
        AiResponse: ItemResponse
    ): Flow<UiState> {
        TODO("Not yet implemented")
    }

    override suspend fun getLostItemData(): Flow<UiState> {
        _getLostItemData.emit(UiState.Loading)
        // TODO implement this function
        return getLostItemData
    }


}