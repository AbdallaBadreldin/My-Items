package com.fstech.myItems.presentation.found

import android.graphics.Bitmap
import android.location.Address
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstech.myItems.BuildConfig.apiKey
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.jetawy.data.repositories.FoundItemsRepositoryImpl
import com.jetawy.domain.models.ItemFound
import com.jetawy.domain.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoundItemViewModel @Inject constructor(private val foundItemsRepositoryImpl: FoundItemsRepositoryImpl) :
    ViewModel() {
    var addresses: MutableList<Address>? = null
    val list = mutableStateListOf<Uri>()
    val latLng = mutableStateOf<LatLng?>(null)
    var userDescription = mutableStateOf<String?>("")

    fun updateItem(itemIndex: Int, item: Uri) {
        list.set(itemIndex, item) // sets the element at 'itemIndex' to 'item'
    } // You can fill the list with initial values if you like, for testing

    fun addItem(uri: Uri?) {
        list.add(uri!!)
    }

    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    var uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val _uploadItems: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    var uploadItems: StateFlow<UiState> =
        _uploadItems.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    fun sendPrompt(
        bitmap: MutableList<Bitmap>,
        prompt: String
    ) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        bitmap.forEach { image(it) }
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    Log.e("outputContent", outputContent)
                    val data = convertJsonToDataClass(outputContent)
                    if (data == null || data.type == "null")
                        _uiState.value = UiState.Error("null")
                    if (data?.type == "null")
                        _uiState.value = UiState.Error("null")
                    else
                        _uiState.value = UiState.Success(
                            data
                        )
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    private fun convertJsonToDataClass(jsonString: String): ItemFound? {
        var string = jsonString.trimIndent().trim()

        if (string.isEmpty()) {
            return null
        }
        string = string.removePrefix("```json").removeSuffix("```")
        val gson = Gson()
        val jsonObject = gson.fromJson(string, ItemFound::class.java)
        return jsonObject
    }

    fun uploadItems(
        imageUris: List<Uri>,
        addresses: Address,
        aiResponse: ItemFound,
    ) {
        viewModelScope.launch {
            try {
                val translateRequest ="translate next string \"${userDescription.value}\" to English if it's not translatable return false"  // or the original string
                val translateResponse = generativeModel.generateContent(
                    content {
                        text(translateRequest)
                    }
                )
                translateResponse.text?.let {
                    aiResponse.translatedDescription= it
                    aiResponse.userDescription = userDescription.value
                }

            }catch (e:Exception){
                _uploadItems.value = UiState.Error(e.localizedMessage ?: "")
            }
            foundItemsRepositoryImpl.uploadFoundItems(
                imageUris,
                addresses = addresses,
                aiResponse,
            ).collect { req ->
                _uploadItems.emit(req)
            }
        }
    }

    fun resetStates() {
        _uiState.value = UiState.Initial
    }

    fun resetUploadItems() {
        viewModelScope.launch {
            _uploadItems.emit(UiState.Initial)
        }
    }

    private val _closeActivity = MutableSharedFlow<Unit>()
    val closeActivity= _closeActivity.asSharedFlow()

    fun triggerCloseActivity() {
        viewModelScope.launch {
            _closeActivity.emit(Unit)
        }
    }
}