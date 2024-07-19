package store.msolapps.flamingo.presentation.home.offers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.request.GetCategoryOffersRequest
import store.msolapps.domain.models.request.ProductsNewRequest
import store.msolapps.domain.models.response.CategoriesResponseModel
import store.msolapps.domain.models.response.CategoryProductsResponseModel
import store.msolapps.domain.models.response.GetDefaultAddressResponse
import store.msolapps.domain.models.response.GetOffersData
import store.msolapps.domain.models.response.OfferName
import store.msolapps.domain.models.response.OffersTitle
import store.msolapps.domain.useCase.cart.GetDefaultAddressUseCase
import store.msolapps.domain.useCase.home.GetProductsUseCase
import store.msolapps.domain.useCase.offers.GetCategoryOffersUseCase
import store.msolapps.domain.useCase.offers.GetOffersDataUseCase
import store.msolapps.domain.useCase.offers.GetOffersNameUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

@HiltViewModel
class ShowOffersViewModel @Inject constructor(
    private val getCategoryOffersUseCase: GetCategoryOffersUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val psp: ProfileSharedPreference
): ViewModel() {

    private val _getCategoryOffersLoading = MutableLiveData<Boolean>(false)
    private val _getCategoryOffersData = MutableLiveData<CategoriesResponseModel?>()
    private val _getCategoryOffersError = MutableLiveData<String>()
    val getCategoryOffersLoading: LiveData<Boolean> get() = _getCategoryOffersLoading
    val getCategoryOffersData: LiveData<CategoriesResponseModel?> get() = _getCategoryOffersData
    val getCategoryOffersError: LiveData<String> get() = _getCategoryOffersError

    private val _getProductsData = MutableLiveData<CategoryProductsResponseModel?>()
    private val _getProductsDataError = MutableLiveData<String>()
    private val _getProductsDataLoading = MutableLiveData<Boolean>(false)

    val getProductsData: LiveData<CategoryProductsResponseModel?> get() = _getProductsData
    val getProductsDataError: LiveData<String> get() = _getProductsDataError
    val getProductsDataLoading: LiveData<Boolean> get() = _getProductsDataLoading
    fun getOffersName(type:String) {
        viewModelScope.launch {
            val catRequest =  GetCategoryOffersRequest(
                psp.getLang(),
                type = type,
                "01"
            )
            getCategoryOffersUseCase.invoke(
                catRequest
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getCategoryOffersData.postValue(requestStatus.data)
                        _getCategoryOffersLoading.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _getCategoryOffersError.postValue(requestStatus.message)
                        _getCategoryOffersLoading.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _getCategoryOffersLoading.postValue(true)
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
        type: String
    ) {
        viewModelScope.launch {
            var category: MutableList<Long> = ArrayList()
            if (!mulCatsIds.isNullOrEmpty())
                category = mulCatsIds
            else
                category.add(categoryId.toLong())


            var mC: MutableList<Long>? = null
            var tY: String? = null
            var mP: MutableList<Long>? = null

            Log.d("TAGaaa","id is $type")
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
                type,
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
    fun clearObservers(){
        _getProductsData.postValue(null)
        _getCategoryOffersData.postValue(null)
    }

}