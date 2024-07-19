package store.msolapps.flamingo.presentation.home.home

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
import store.msolapps.domain.models.response.SpecialCategoriesResponse
import store.msolapps.domain.models.response.StickyBannersResponse
import store.msolapps.domain.useCase.auth.SendTokenUseCase
import store.msolapps.domain.useCase.cart.GetDefaultAddressUseCase
import store.msolapps.domain.useCase.home.GetCategoriesHomeUseCase
import store.msolapps.domain.useCase.home.GetCategoriesUseCase
import store.msolapps.domain.useCase.home.GetHomeBannersUseCase
import store.msolapps.domain.useCase.home.GetSpecialCategoriesUseCase
import store.msolapps.domain.useCase.home.GetStickyBannersUseCase
import store.msolapps.domain.utils.RequestStatus
import store.msolapps.flamingo.util.ConnectionStatus
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeBannersUseCase: GetHomeBannersUseCase,
    private val getSpecialCategoriesUseCase: GetSpecialCategoriesUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getStickyBannersUseCase: GetStickyBannersUseCase,
    private val getDefaultAddressUseCase: GetDefaultAddressUseCase,
    private val getCategoriesHomeUseCase: GetCategoriesHomeUseCase,
    private val sendTokenUseCase: SendTokenUseCase,
    private val psp: ProfileSharedPreference,
    private val asp: AuthSharedPreference,
    val connection: ConnectionStatus,
) : ViewModel() {
    //getCategories
    private val _getCategoriesData = MutableLiveData<CategoriesResponseModel?>()
    private val _getCategoriesError = MutableLiveData<String>()
    private val _getCategoriesLoading = MutableLiveData<Boolean>(false)
    val getCategoriesData: LiveData<CategoriesResponseModel?> get() = _getCategoriesData
    val getCategoriesError: LiveData<String> get() = _getCategoriesError
    val getCategoriesLoading: LiveData<Boolean> get() = _getCategoriesLoading

    //home banners
    private val _getHomeBannersData = MutableLiveData<HomeBannersResponse?>()
    private val _getHomeBannersError = MutableLiveData<String>()
    private val _getHomeBannersLoading = MutableLiveData<Boolean>(false)
    val getHomeBannersData: LiveData<HomeBannersResponse?> get() = _getHomeBannersData
    val getHomeBannersError: LiveData<String> get() = _getHomeBannersError
    val getHomeBannersLoading: LiveData<Boolean> get() = _getHomeBannersLoading

    //special deals
    private val _getSpecialCategoryData = MutableLiveData<SpecialCategoriesResponse?>()
    private val _getSpecialCategoryError = MutableLiveData<String>()
    private val _getSpecialCategoryLoading = MutableLiveData<Boolean>(false)
    val getSpecialCategoryData: LiveData<SpecialCategoriesResponse?> get() = _getSpecialCategoryData
    val getSpecialCategoryError: LiveData<String> get() = _getSpecialCategoryError
    val getSpecialCategoryLoading: LiveData<Boolean> get() = _getSpecialCategoryLoading

    //sticky banners
    private val _getStickyBannersData = MutableLiveData<StickyBannersResponse?>()
    private val _getStickyBannersError = MutableLiveData<String>()
    private val _getStickyBannersLoading = MutableLiveData<Boolean>(false)
    val getStickyBannersData: LiveData<StickyBannersResponse?> get() = _getStickyBannersData
    val getStickyBannersError: LiveData<String> get() = _getStickyBannersError
    val getStickyBannersLoading: LiveData<Boolean> get() = _getStickyBannersLoading

    private val _getCategoriesHomeData = MutableLiveData<CategoriesResponseModel?>()
    private val _getCategoriesHomeError = MutableLiveData<String>()
    private val _getCategoriesHomeLoading = MutableLiveData<Boolean>(false)
    val getCategoriesHomeData: LiveData<CategoriesResponseModel?> get() = _getCategoriesHomeData
    val getCategoriesHomeError: LiveData<String> get() = _getCategoriesHomeError
    val getCategoriesHomeLoading: LiveData<Boolean> get() = _getCategoriesHomeLoading

    private val _tokenProductsLiveData = MutableLiveData<TokenResponse>()
    val tokenProductsLiveData: LiveData<TokenResponse?> get() = _tokenProductsLiveData

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

    fun getSpecialCategories() {
        viewModelScope.launch {
            getSpecialCategoriesUseCase.invoke().collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getSpecialCategoryData.postValue(requestStatus.data)
                        _getSpecialCategoryLoading.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _getSpecialCategoryError.postValue(requestStatus.message)
                        _getSpecialCategoryLoading.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _getSpecialCategoryLoading.postValue(true)
                    }
                }
            }
        }
    }

    fun getHomeBanners() {
        viewModelScope.launch {
            getHomeBannersUseCase.invoke().collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getHomeBannersData.postValue(requestStatus.data)
                        _getHomeBannersLoading.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _getHomeBannersError.postValue(requestStatus.message)
                        _getHomeBannersLoading.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _getHomeBannersLoading.postValue(true)
                    }
                }
            }
        }
    }

    fun getStickyBanners() {
        viewModelScope.launch {
            getStickyBannersUseCase.invoke().collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getStickyBannersData.postValue(requestStatus.data)
                        _getStickyBannersLoading.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _getStickyBannersError.postValue(requestStatus.message)
                        _getStickyBannersLoading.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _getStickyBannersLoading.postValue(true)
                    }
                }
            }
        }
    }

    fun getCategoriesHome() {
        viewModelScope.launch {
            getCategoriesHomeUseCase.invoke(
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getCategoriesHomeData.postValue(requestStatus.data)
                        _getCategoriesHomeLoading.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _getCategoriesHomeError.postValue(requestStatus.message)
                        _getCategoriesHomeLoading.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _getCategoriesHomeLoading.postValue(true)
                    }
                }
            }
        }
    }


    private val _getDefaultAddressLoad = MutableLiveData<Boolean>(false)
    private val _getDefaultAddressData = MutableLiveData<GetDefaultAddressResponse?>()
    private val _getDefaultAddressError = MutableLiveData<String>()
    val getDefaultAddressLoad: LiveData<Boolean> get() = _getDefaultAddressLoad
    val getDefaultAddressData: LiveData<GetDefaultAddressResponse?> get() = _getDefaultAddressData
    val getDefaultAddressError: LiveData<String> get() = _getDefaultAddressError

    fun getDefaultAddress() {
        viewModelScope.launch {
            getDefaultAddressUseCase.invoke().collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getDefaultAddressData.postValue(requestStatus.data)
                        _getDefaultAddressLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _getDefaultAddressError.postValue(requestStatus.message)
                        _getDefaultAddressLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _getDefaultAddressLoad.postValue(true)
                    }
                }
            }
        }
    }

    fun sendToken(tokenRequest: TokenRequest) {
        viewModelScope.launch {
            sendTokenUseCase.invoke(tokenRequest).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _tokenProductsLiveData.postValue(requestStatus.data)
                    }
                    is RequestStatus.Error -> {}

                    is RequestStatus.Waiting -> {}
                }
            }
        }
    }

    fun clearObservers() {
        _getHomeBannersData.postValue(null)
        _getSpecialCategoryData.postValue(null)
        _getCategoriesHomeData.postValue(null)
        _getStickyBannersData.postValue(null)
        _getCategoriesData.postValue(null)
        _getDefaultAddressData.postValue(null)
    }

    fun isUserLoggedIn() = asp.isLogged()
    fun getUserName() = asp.getUserName()
    fun getToken(): String {
        return asp.getToken()
    }
    fun getLang():String{
        return psp.getLang()
    }

    fun setIsDefaultAddressExist(b: Boolean) {
      psp.setIsDefaultAddressExist(b)
    }
}