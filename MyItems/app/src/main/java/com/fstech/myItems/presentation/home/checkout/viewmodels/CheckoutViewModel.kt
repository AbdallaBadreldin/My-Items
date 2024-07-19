package store.msolapps.flamingo.presentation.home.checkout.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.RemoteConfigConstants.RequestFieldKey.SDK_VERSION
import store.msolapps.domain.models.request.CheckoutPostModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.AuthSharedPreference
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.paymob.response.IframeResponse
import store.msolapps.domain.models.paymob.response.PaymobClientSecret
import store.msolapps.domain.models.request.GetPaidPostModel
import store.msolapps.domain.models.response.CartResponseModel
import store.msolapps.domain.models.response.Checkout.CheckoutResponseModel
import store.msolapps.domain.models.response.GetDefaultAddressResponse
import store.msolapps.domain.models.response.GetPaid.GetPaidResponseModel
import store.msolapps.domain.models.response.GetPaymentMethods
import store.msolapps.domain.useCase.cart.CreateOrderUseCase
import store.msolapps.domain.useCase.cart.GetCartUseCase
import store.msolapps.domain.useCase.cart.GetClientSecretUseCase
import store.msolapps.domain.useCase.cart.GetDefaultAddressUseCase
import store.msolapps.domain.useCase.cart.GetIFrameUseCase
import store.msolapps.domain.useCase.cart.GetPaidTypesUseCase
import store.msolapps.domain.useCase.cart.GetPaymentMethodUseCase
import store.msolapps.domain.useCase.cart.GetSlotsUseCase
import store.msolapps.domain.utils.RequestStatus
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val createOrderUseCase: CreateOrderUseCase,
    private val getPaidTypesUseCase: GetPaidTypesUseCase,
    private val getPaymentMethodUseCase: GetPaymentMethodUseCase,
    private val getCartUseCase: GetCartUseCase,
    private val getDefaultAddressUseCase: GetDefaultAddressUseCase,
    private val getSlotsUseCase: GetSlotsUseCase,
    private val getClientSecretUseCase: GetClientSecretUseCase,
    private val getIFrameUseCase: GetIFrameUseCase,
    private val psp: ProfileSharedPreference,
    private val asp: AuthSharedPreference,
) : ViewModel() {

    private val _createOrderLoad = MutableLiveData<Boolean>(false)
    private val _createOrderData = MutableLiveData<CheckoutResponseModel?>()
    private val _createOrderError = MutableLiveData<String>()
    val createOrderLoad: LiveData<Boolean> get() = _createOrderLoad
    val createOrderData: LiveData<CheckoutResponseModel?> get() = _createOrderData
    val createOrderError: LiveData<String> get() = _createOrderError



    fun createOrder(
        checkoutPostModel: CheckoutPostModel
    ) {
        viewModelScope.launch {
            createOrderUseCase.invoke(checkoutPostModel, "Android", SDK_VERSION)
                .collect { requestStatus ->
                    when (requestStatus) {
                        is RequestStatus.Success -> {
                            _createOrderData.postValue(requestStatus.data)
                            _createOrderLoad.postValue(false)
                        }

                        is RequestStatus.Error -> {
                            _createOrderError.postValue(requestStatus.message)
                            _createOrderLoad.postValue(false)
                        }

                        is RequestStatus.Waiting -> {
                            _createOrderLoad.postValue(true)
                        }
                    }
                }
        }
    }

    private val _getPaidTypesLoading = MutableLiveData<Boolean>(false)
    private val _getPaidTypesData = MutableLiveData<GetPaidResponseModel>()
    private val _getPaidTypesError = MutableLiveData<String>()
    val getPaidTypesLoading: LiveData<Boolean> get() = _getPaidTypesLoading
    val getPaidTypesData: LiveData<GetPaidResponseModel> get() = _getPaidTypesData
    val getPaidTypesError: LiveData<String> get() = _getPaidTypesError

    fun getPaidTypes(
    ) {
        val coordinates =
            "${getDefaultAddressData.value?.data?.lat.toString()},${getDefaultAddressData.value?.data?.lng.toString()}"
        val postModel = GetPaidPostModel(
            psp.getLang(), psp.getStoreId(),
            coordinates,
            0
        )
        viewModelScope.launch {
            getPaidTypesUseCase.invoke(postModel).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getPaidTypesData.postValue(requestStatus.data)
                        _getPaidTypesLoading.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _getPaidTypesError.postValue(requestStatus.message)
                        _getPaidTypesLoading.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _getPaidTypesLoading.postValue(true)
                    }
                }
            }
        }
    }

    private val _getPaymentMethodLoad = MutableLiveData<Boolean>(false)
    private val _getPaymentMethodData = MutableLiveData<GetPaymentMethods>()
    private val _getPaymentMethodError = MutableLiveData<String>()
    val getPaymentMethodLoad: LiveData<Boolean> get() = _getPaymentMethodLoad
    val getPaymentMethodData: LiveData<GetPaymentMethods> get() = _getPaymentMethodData
    val getPaymentMethodError: LiveData<String> get() = _getPaymentMethodError

    fun getPaymentMethod(
    ) {
        viewModelScope.launch {
            getPaymentMethodUseCase.invoke().collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getPaymentMethodData.postValue(requestStatus.data)
                        _getPaymentMethodLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _getPaymentMethodError.postValue(requestStatus.message)
                        _getPaymentMethodLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _getPaymentMethodLoad.postValue(true)
                    }
                }
            }
        }
    }

    private val _cartLoad = MutableLiveData<Boolean>(false)
    private val _cartData = MutableLiveData<CartResponseModel>()
    private val _cartError = MutableLiveData<String>()
    val cartLoad: LiveData<Boolean> get() = _cartLoad
    val cartData: LiveData<CartResponseModel> get() = _cartData
    val cartError: LiveData<String> get() = _cartError

    fun getCartData() {
        viewModelScope.launch {
            getCartUseCase.invoke().collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _cartData.postValue(requestStatus.data)
                        _cartLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _cartError.postValue(requestStatus.message)
                        _cartLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _cartLoad.postValue(true)
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

    private val _getPaymobClientSecretLoad = MutableLiveData<Boolean>(false)
    private val _getPaymobClientSecretData = MutableLiveData<PaymobClientSecret>()
    private val _getPaymobClientSecretError = MutableLiveData<String>()
    val getPaymobClientSecretLoad: LiveData<Boolean> get() = _getPaymobClientSecretLoad
    val getPaymobClientSecretData: LiveData<PaymobClientSecret> get() = _getPaymobClientSecretData
    val getPaymobClientSecretError: LiveData<String> get() = _getPaymobClientSecretError

    fun getPaymobClientSecret(amount: Double) {
        viewModelScope.launch {
            getClientSecretUseCase.invoke(amount = amount).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getPaymobClientSecretData.postValue(requestStatus.data)
                        _getPaymobClientSecretLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _getPaymobClientSecretError.postValue(requestStatus.message)
                        _getPaymobClientSecretLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _getPaymobClientSecretLoad.postValue(true)
                    }
                }
            }
        }
    }

    private val _getIframeLoad = MutableLiveData<Boolean>(false)
    private val _getIframeData = MutableLiveData<IframeResponse?>()
    private val _getIframeError = MutableLiveData<String>()
    val getIframeLoad: LiveData<Boolean> get() = _getIframeLoad
    val getIframeData: LiveData<IframeResponse?> get() = _getIframeData
    val getIframeError: LiveData<String> get() = _getIframeError

    fun getPaymobIframe(addressId:Int,total:Double) {
        viewModelScope.launch {
            getIFrameUseCase.invoke(addressId, total).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _getIframeData.postValue(requestStatus.data)
                        _getIframeLoad.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _getIframeError.postValue(requestStatus.message)
                        _getIframeLoad.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _getIframeLoad.postValue(true)
                    }
                }
            }
        }
    }
    fun getLang() = psp.getLang()
    fun getStoreId() = psp.getStoreId()

    fun clearObserver(){
        _getIframeData.postValue(null)
        _createOrderData.value = null
        _createOrderData.value = null
        _getDefaultAddressData.value =null
    }

}