package store.msolapps.flamingo.presentation.home.products

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.AuthSharedPreference
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.request.TokenRequest
import store.msolapps.domain.models.request.TokenResponse
import store.msolapps.domain.models.response.CategoriesResponseModel
import store.msolapps.domain.models.response.GetDefaultAddressResponse
import store.msolapps.domain.models.response.HomeBannersResponse
import store.msolapps.domain.models.response.ProductResponseModel
import store.msolapps.domain.models.response.SpecialCategoriesResponse
import store.msolapps.domain.models.response.StickyBannersResponse
import store.msolapps.domain.useCase.auth.SendTokenUseCase
import store.msolapps.domain.useCase.cart.GetDefaultAddressUseCase
import store.msolapps.domain.useCase.home.GetCategoriesHomeUseCase
import store.msolapps.domain.useCase.home.GetCategoriesUseCase
import store.msolapps.domain.useCase.home.GetHomeBannersUseCase
import store.msolapps.domain.useCase.home.GetProductDataUseCase
import store.msolapps.domain.useCase.home.GetSpecialCategoriesUseCase
import store.msolapps.domain.useCase.home.GetStickyBannersUseCase
import store.msolapps.domain.utils.RequestStatus
import store.msolapps.flamingo.util.ConnectionStatus
import javax.inject.Inject

@HiltViewModel
class ShowProductFromHomeViewModel @Inject constructor(
    private val getProductDataUseCase: GetProductDataUseCase,
    private val psp: ProfileSharedPreference
) : ViewModel() {
    //getCategories
    private val _getProductData = MutableLiveData<ProductResponseModel?>()
    private val _getProductDataError = MutableLiveData<String>()
    private val _getProductDataLoading = MutableLiveData<Boolean>(false)
    val getProductData: LiveData<ProductResponseModel?> get() = _getProductData
    val getProductDataError: LiveData<String> get() = _getProductDataError
    val getProductDataLoading: LiveData<Boolean> get() = _getProductDataLoading

    fun getProductData(
        product_id: String,
    ) {
        viewModelScope.launch {
            getProductDataUseCase.invoke(
                product_id, psp.getLang(), "01"
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getProductData.postValue(requestStatus.data)
                        _getProductDataLoading.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _getProductDataError.postValue(requestStatus.message)
                        Log.d("AAAAA","${requestStatus.message}")
                        _getProductDataLoading.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _getProductDataLoading.postValue(true)
                    }
                }
            }
        }
    }
}