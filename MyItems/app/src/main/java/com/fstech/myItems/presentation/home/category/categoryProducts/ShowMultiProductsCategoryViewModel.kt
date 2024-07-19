package store.msolapps.flamingo.presentation.home.category.categoryProducts

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.request.DeepMultiCatsRequest
import store.msolapps.domain.models.request.GetCategoryMultiProductsRequest
import store.msolapps.domain.models.request.ProductsNewRequest
import store.msolapps.domain.models.response.CategoriesResponseModel
import store.msolapps.domain.models.response.CategoryProductsResponseModel
import store.msolapps.domain.models.response.ProductResponseModel
import store.msolapps.domain.useCase.home.GetCategoriesUseCase
import store.msolapps.domain.useCase.home.GetDeepCategoryHomeUseCase
import store.msolapps.domain.useCase.home.GetProductCategoriesUseCase
import store.msolapps.domain.useCase.home.GetProductsUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

@HiltViewModel
class ShowMultiProductsCategoryViewModel @Inject constructor(
    private val getProductCategoriesUseCase: GetProductCategoriesUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val psp: ProfileSharedPreference
) : ViewModel() {
    private val _getCategoriesData = MutableLiveData<CategoriesResponseModel?>()
    private val _getCategoriesError = MutableLiveData<String>()
    private val _getCategoriesLoading = MutableLiveData<Boolean>(false)

    val getCategoriesData: LiveData<CategoriesResponseModel?> get() = _getCategoriesData
    val getCategoriesError: LiveData<String> get() = _getCategoriesError
    val getCategoriesLoading: LiveData<Boolean> get() = _getCategoriesLoading

    private val _getProductsData = MutableLiveData<CategoryProductsResponseModel?>()
    private val _getProductsDataError = MutableLiveData<String>()
    private val _getProductsDataLoading = MutableLiveData<Boolean>(false)

    val getProductsData: LiveData<CategoryProductsResponseModel?> get() = _getProductsData
    val getProductsDataError: LiveData<String> get() = _getProductsDataError
    val getProductsDataLoading: LiveData<Boolean> get() = _getProductsDataLoading

    fun getCategories(products: MutableList<Long>) {
        viewModelScope.launch {
            val request =
                GetCategoryMultiProductsRequest(psp.getLang(), products, "01")
            getProductCategoriesUseCase.invoke(
                request
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

    fun getProducts(
        categoryId: String,
        page: Int,
        paginate: Int,
        offer: Int? = 1,
        min: Int? = null,
        max: Int? = null,
        sort: String? = null,
        mulCatsIds: MutableList<Long>? = null,
        multiProducts: MutableList<Long>? = null
    ) {
        viewModelScope.launch {
            var category: MutableList<Long> = ArrayList()
            if (!mulCatsIds.isNullOrEmpty())
                category = mulCatsIds
            else
                category.add(categoryId.toLong())

            val mC: MutableList<Long>? = null
            val tY: String? = null
            val mP: MutableList<Long>? = multiProducts
            Log.d("AAAAA","ids $mP")
            val request = ProductsNewRequest(
                "01",
                psp.getLang(),
                paginate,
                page,
                category,
                offer!!,
                min,
                max,
                sort,
                mC,
                tY,
                mP
            )
            getProductsUseCase.invoke(
                request
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getProductsData.postValue(requestStatus.data)
                        _getProductsDataLoading.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _getProductsDataError.postValue(requestStatus.message)
                        _getProductsDataLoading.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _getProductsDataLoading.postValue(true)
                    }
                }
            }
        }
    }
    fun clearObserver(){
        _getCategoriesData.postValue(null)
        _getProductsData.postValue(null)
    }
}