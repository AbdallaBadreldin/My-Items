package store.msolapps.flamingo.presentation.home.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.request.DeleteAddressRequest
import store.msolapps.domain.models.request.UpdateAddressAuo
import store.msolapps.domain.models.response.AddressResponseModel
import store.msolapps.domain.models.response.DeleteAddressResponseModel
import store.msolapps.domain.models.response.UpdateDefaultAddressResponse
import store.msolapps.domain.useCase.home.UpdateDefaultAddressUseCase
import store.msolapps.domain.useCase.profile.DeleteAddressUseCase
import store.msolapps.domain.useCase.profile.GetAddressesUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

private const val TAG = "MyAddViewModel_TAG"

@HiltViewModel
class MyAddressesFragmentViewModel @Inject constructor(
    private val getAddressesUseCase: GetAddressesUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    private val updateDefaultAddressUseCase: UpdateDefaultAddressUseCase,
    private val psp: ProfileSharedPreference
) : ViewModel() {
    private val _isLoad = MutableLiveData<Boolean>(false)
    private val _getAddresses = MutableLiveData<AddressResponseModel>()
    private val _error = MutableLiveData<String>()
    val isLoad: LiveData<Boolean> get() = _isLoad
    val getAddresses: LiveData<AddressResponseModel> get() = _getAddresses
    val error: LiveData<String> get() = _error

    private val _isLoadDelete = MutableLiveData<Boolean>(false)
    private val _deleteAddresses = MutableLiveData<DeleteAddressResponseModel>()
    private val _errorDelete = MutableLiveData<String>()
    val isLoadDelete: LiveData<Boolean> get() = _isLoadDelete
    val deleteAddresses: LiveData<DeleteAddressResponseModel> get() = _deleteAddresses
    val errorDelete: LiveData<String> get() = _errorDelete
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

    fun deleteAddressAPI(id: String) {
        viewModelScope.launch {
            deleteAddressUseCase
                .invoke(id, DeleteAddressRequest(psp.getLang()))
                .collect { requestStatus ->
                    when (requestStatus) {
                        is RequestStatus.Success -> {
                            _deleteAddresses.postValue(requestStatus.data)
                            _isLoadDelete.postValue(false)
                        }

                        is RequestStatus.Error -> {
                            _errorDelete.postValue(requestStatus.message)
                            _isLoadDelete.postValue(false)
                        }

                        is RequestStatus.Waiting -> {
                            _isLoadDelete.postValue(true)
                        }
                    }
                }
        }
    }
    private val _updateDefaultAddresses = MutableLiveData<UpdateDefaultAddressResponse?>()
    private val _errorUpdate = MutableLiveData<String>()
    val updateDefaultAddresses: LiveData<UpdateDefaultAddressResponse?> get() = _updateDefaultAddresses
    val errorUpdate: LiveData<String> get() = _errorUpdate
    fun updateDefaultAddress(addressId: Int) {
        viewModelScope.launch {
            val updateAddressAuo = UpdateAddressAuo(addressId)
            updateDefaultAddressUseCase.invoke(
                updateAddressAuo
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _updateDefaultAddresses.postValue(requestStatus.data)
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
}

