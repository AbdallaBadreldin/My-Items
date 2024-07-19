package store.msolapps.flamingo.presentation.home.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msolapps.oscar.Model.ResponseModel.ProductsOrdersResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.request.FilterSearchProductsRequest
import store.msolapps.domain.models.response.FilterSearchResponse
import store.msolapps.domain.useCase.general.GetProductsForYouUseCase
import store.msolapps.domain.useCase.general.GetSearchProductsUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getSearchProductsUseCase: GetSearchProductsUseCase,
    private val getProductsForYouUseCase: GetProductsForYouUseCase,
    private val psp: ProfileSharedPreference
) : ViewModel() {
    private val _getSearchProductsData = MutableLiveData<FilterSearchResponse?>()
    private val _getSearchProductsError = MutableLiveData<String>()
    private val _getSearchProductsLoading = MutableLiveData<Boolean>(false)
    private val _getProductsForYou = MutableLiveData<ProductsOrdersResponse>()
    private val _getProductsForYouError = MutableLiveData<String>()
    private val _getProductsForYouLoading = MutableLiveData<Boolean>(false)

    val getSearchProductsData: LiveData<FilterSearchResponse?> get() = _getSearchProductsData
    val getSearchProductsError: LiveData<String> get() = _getSearchProductsError
    val getSearchProductsLoading: LiveData<Boolean> get() = _getSearchProductsLoading
    val getProductsForYou: LiveData<ProductsOrdersResponse> get() = _getProductsForYou
    val getProductsForYouError: LiveData<String> get() = _getProductsForYouError
    val getProductsForYouLoading: LiveData<Boolean> get() = _getProductsForYouLoading

    var page: Int = 1
    var totalPages = 1
    val paginate: Int = 10

     fun filterSearchProduct(
        context: Context,
        from: Int,
        searchQuery: String,
        categoriesIds: MutableList<Long>,
        offer: Int? = 1,
        min: Int? = null,
        max: Int? = null,
        sort: String? = null,
        recall: Boolean? = false,
    ) {
         viewModelScope.launch {
             if (recall!!) {
                 page = 1
                 totalPages = 1
             }

             if (page <= totalPages) {
                 val request = FilterSearchProductsRequest(
                     searchQuery,
                     "01",
                     psp.getLang(),
                     paginate,
                     page,
                     categoriesIds,
                     offer!!,
                     min,
                     max,
                     sort
                 )
                 getSearchProductsUseCase.invoke(request).collect { requestStatus ->
                     when (requestStatus) {
                         is RequestStatus.Success -> {
                             _getSearchProductsData.postValue(requestStatus.data)
                             totalPages = requestStatus.data.data.last_page
                             Log.d("searchVM","$totalPages and $page")
                             _getSearchProductsLoading.postValue(false)
                         }

                         is RequestStatus.Error -> {
                             _getSearchProductsError.postValue(requestStatus.message)
                             _getSearchProductsLoading.postValue(false)
                         }

                         is RequestStatus.Waiting -> {
                             _getSearchProductsLoading.postValue(true)
                         }
                     }
                     page++
                 }
             }
         }
    }
    fun getProductsForYou() {
        viewModelScope.launch {
            getProductsForYouUseCase.invoke(
                psp.getLang(),
                "01"
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getProductsForYou.postValue(requestStatus.data)
                        _getProductsForYouLoading.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _getProductsForYouError.postValue(requestStatus.message)
                        _getProductsForYouLoading.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _getProductsForYouLoading.postValue(true)
                    }
                }
            }
        }
    }
    fun clearObserver(){
        _getSearchProductsData.postValue(null)
    }
}