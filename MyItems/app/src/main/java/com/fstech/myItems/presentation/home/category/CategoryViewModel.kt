package store.msolapps.flamingo.presentation.home.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.domain.models.response.CategoriesResponseModel
import store.msolapps.domain.useCase.home.GetCategoriesUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _getCategoriesData = MutableLiveData<CategoriesResponseModel?>()
    private val _getCategoriesError = MutableLiveData<String>()
    private val _getCategoriesLoading = MutableLiveData<Boolean>(false)

    val getCategoriesData: LiveData<CategoriesResponseModel?> get() = _getCategoriesData
    val getCategoriesError: LiveData<String> get() = _getCategoriesError
    val getCategoriesLoading: LiveData<Boolean> get() = _getCategoriesLoading

    fun getCategories() {
        viewModelScope.launch {
            getCategoriesUseCase.invoke(
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getCategoriesData.postValue(requestStatus.data)
                        _getCategoriesLoading.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _getCategoriesError.postValue(requestStatus.message)
                        _getCategoriesLoading.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _getCategoriesLoading.postValue(true)
                    }
                }
            }
        }
    }
    fun clearObservers(){
        _getCategoriesData.postValue(null)
    }
}