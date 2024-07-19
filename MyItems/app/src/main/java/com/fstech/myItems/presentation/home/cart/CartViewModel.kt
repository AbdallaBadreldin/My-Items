package store.msolapps.flamingo.presentation.home.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.AuthSharedPreference
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.response.CartResponseModel
import store.msolapps.domain.useCase.cart.GetCartUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val localDataSource: AuthSharedPreference,
    private val psp: ProfileSharedPreference,
) : ViewModel() {
    private val _isLoad = MutableLiveData<Boolean>(false)
    private val _cart = MutableLiveData<CartResponseModel>()
    private val _error = MutableLiveData<String>()
    val isLoad: LiveData<Boolean> get() = _isLoad
    val cart: LiveData<CartResponseModel> get() = _cart
    val error: LiveData<String> get() = _error

    fun getCartData() {
        viewModelScope.launch {
            getCartUseCase.invoke().collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _cart.postValue(requestStatus.data)
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

    fun isDefaultAddressExist(): Boolean {
       return psp.getIsDefaultAddressExist()
    }
}