package store.msolapps.flamingo.presentation.home.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.domain.models.request.UpdateAddressAuo
import store.msolapps.domain.models.response.AddressResponseModel
import store.msolapps.domain.models.response.UpdateDefaultAddressResponse
import store.msolapps.domain.useCase.home.UpdateDefaultAddressUseCase
import store.msolapps.domain.useCase.profile.GetAddressesUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

@HiltViewModel
class AddressBottomSheetViewModel @Inject constructor(
    private val getAddressesUseCase: GetAddressesUseCase,
    private val updateAddressesUseCase: UpdateDefaultAddressUseCase
) : ViewModel() {
    private val _isLoad = MutableLiveData<Boolean>(false)
    private val _getAddresses = MutableLiveData<AddressResponseModel?>()
    private val _error = MutableLiveData<String>()
    val getAddresses: LiveData<AddressResponseModel?> get() = _getAddresses
    val error: LiveData<String> get() = _error

    private val _updateAddresses = MutableLiveData<UpdateDefaultAddressResponse?>()
    private val _errorUpdate = MutableLiveData<String>()
    val updateAddresses: LiveData<UpdateDefaultAddressResponse?> get() = _updateAddresses
    val errorUpdate: LiveData<String> get() = _errorUpdate

    val isLoad: LiveData<Boolean> get() = _isLoad

    fun getAddressAPI() {
        viewModelScope.launch {
            getAddressesUseCase.invoke().collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getAddresses.postValue(requestStatus.data)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _error.postValue(requestStatus.message)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoad.postValue(true)
                    }
                }
            }
        }
    }

    fun updateAddress(addressId: Int) {
        viewModelScope.launch {
            val updateAddressAuo = UpdateAddressAuo(addressId)
            updateAddressesUseCase.invoke(
                updateAddressAuo
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _updateAddresses.postValue(requestStatus.data)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _error.postValue(requestStatus.message)
                        _isLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoad.postValue(true)
                    }
                }
            }
        }
    }

    fun clearObservers() {
        _updateAddresses.postValue(null)
        _getAddresses.postValue(null)
    }
}