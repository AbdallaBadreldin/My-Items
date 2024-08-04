package com.fstech.myItems.presentation.matchmaking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstech.myItems.BuildConfig.apiKey
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jetawy.data.repositories.FoundItemsRepositoryImpl
import com.jetawy.data.repositories.LostItemsRepositoryImpl
import com.jetawy.domain.models.get.found.ItemFoundResponse
import com.jetawy.domain.repository.ChatRepository
import com.jetawy.domain.repository.FoundItemsRepository
import com.jetawy.domain.repository.LostItemsRepository
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
class MatchMakingViewModel @Inject constructor(
    private val foundItemsRepo: FoundItemsRepository,
    private val lostItemsRepo: LostItemsRepository,
    private val chatRepo: ChatRepository,
) : ViewModel() {
    var itemIndex = 0
    var detailIndex = 0

    private val _foundUiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val foundUiState: StateFlow<UiState> =
        _foundUiState.asStateFlow()

    private val _lostUiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val lostUiState: StateFlow<UiState> =
        _lostUiState.asStateFlow()

    private val _getFoundUiStateByCountry: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val getFoundUiStateByCountry: StateFlow<UiState> =
        _getFoundUiStateByCountry.asStateFlow()

    fun getFoundItemByCountry(country: String) {
        viewModelScope.launch {
            foundItemsRepo.getFoundItemsByCountry(country).collect {
                _getFoundUiStateByCountry.emit(it)
            }
        }
    }

    fun getFoundItemById() {
        viewModelScope.launch {
            foundItemsRepo.getFoundItemsById().collect {
                _foundUiState.emit(it)
            }
        }
    }

    fun getLostItemById() {
        viewModelScope.launch {
            lostItemsRepo.getLostItemById().collect {
                _lostUiState.emit(it)
            }
        }
    }

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )
    private val _promptState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val promptState: StateFlow<UiState> =
        _promptState.asStateFlow()

    fun sendPrompt(
        prompt: String,
    ) {
        _promptState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    // Handle the generated text
                    val data = parseJsonArray(outputContent)
                    _promptState.value = UiState.Success<List<ItemFoundResponse>>(data)
                }
            } catch (e: Exception) {
                _promptState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    private fun parseJsonArray(jsonString: String): List<ItemFoundResponse> {
        var string = jsonString.trimIndent().trim()

        if (string.isEmpty()) {
            return emptyList()
        }
        string = jsonString.removePrefix("```json").removeSuffix("```")
        val gson = Gson()
        val listType = object : TypeToken<List<ItemFoundResponse>>() {}.type
        return gson.fromJson(string, listType)
    }

    private val _closeActivity = MutableSharedFlow<Unit>()
    val closeActivity = _closeActivity.asSharedFlow()

    fun triggerCloseActivity() {
        viewModelScope.launch {
            _closeActivity.emit(Unit)
        }
    }

    fun resetPromptState() {
        _promptState.value = UiState.Initial
    }

    fun resetFoundItems() {
    }

    fun sendMessage(objectID: String?,sender:String,receiver:String)  {

    }

}