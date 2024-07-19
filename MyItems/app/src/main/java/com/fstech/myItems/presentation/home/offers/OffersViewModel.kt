package store.msolapps.flamingo.presentation.home.offers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.response.CategoriesResponseModel
import store.msolapps.domain.models.response.GetDefaultAddressResponse
import store.msolapps.domain.models.response.GetOffersData
import store.msolapps.domain.models.response.OfferName
import store.msolapps.domain.models.response.OffersTitle
import store.msolapps.domain.useCase.cart.GetDefaultAddressUseCase
import store.msolapps.domain.useCase.offers.GetOffersDataUseCase
import store.msolapps.domain.useCase.offers.GetOffersNameUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

@HiltViewModel
class OffersViewModel @Inject constructor(
    private val getOffersNameUseCase: GetOffersNameUseCase,
    private val getOffersDataUseCase: GetOffersDataUseCase,
    private val getDefaultAddressUseCase: GetDefaultAddressUseCase,
    private val psp: ProfileSharedPreference
): ViewModel() {

    private val _getOfferNames = MutableLiveData<OffersTitle?>()
    private val _getOfferNamesError = MutableLiveData<String>()
    private val _getOfferNamesLoading = MutableLiveData<Boolean>(false)

    val getOfferNames: LiveData<OffersTitle?> get() = _getOfferNames
    val getOfferNamesError: LiveData<String> get() = _getOfferNamesError
    val getOfferNamesLoading: LiveData<Boolean> get() = _getOfferNamesLoading

    private val _getOfferData = MutableLiveData<GetOffersData?>()
    private val _getOfferDataError = MutableLiveData<String>()
    private val _getOfferDataLoading = MutableLiveData<Boolean>(false)

    val getOfferData: LiveData<GetOffersData?> get() = _getOfferData
    val getOfferDataError: LiveData<String> get() = _getOfferDataError
    val getOfferDataLoading: LiveData<Boolean> get() = _getOfferDataLoading

    private val _getDefaultAddressLoad = MutableLiveData<Boolean>(false)
    private val _getDefaultAddressData = MutableLiveData<GetDefaultAddressResponse?>()
    private val _getDefaultAddressError = MutableLiveData<String>()
    val getDefaultAddressLoad: LiveData<Boolean> get() = _getDefaultAddressLoad
    val getDefaultAddressData: LiveData<GetDefaultAddressResponse?> get() = _getDefaultAddressData
    val getDefaultAddressError: LiveData<String> get() = _getDefaultAddressError

    fun getOffersName() {
        viewModelScope.launch {
            getOffersNameUseCase.invoke(
                psp.getLang(),
                "01"
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getOfferNames.postValue(requestStatus.data)
                        _getOfferNamesLoading.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _getOfferNamesError.postValue(requestStatus.message)
                        _getOfferNamesLoading.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _getOfferNamesLoading.postValue(true)
                    }
                }
            }
        }
    }

    fun getOffersData() {
        viewModelScope.launch {
            getOffersDataUseCase.invoke(
                psp.getLang(),
                "01"
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getOfferData.postValue(requestStatus.data)
                        _getOfferDataLoading.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _getOfferDataError.postValue(requestStatus.message)
                        _getOfferDataLoading.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _getOfferDataLoading.postValue(true)
                    }
                }
            }
        }
    }

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
    fun getLanguage():String {
        return psp.getLang()
    }
    fun clearObservers(){
        _getOfferNames.postValue(null)
        _getOfferData.postValue(null)
        _getDefaultAddressData.postValue(null)
    }
}