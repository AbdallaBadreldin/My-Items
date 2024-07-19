package store.msolapps.flamingo.presentation.home.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.domain.models.request.AddToCartPostModel
import store.msolapps.domain.models.request.RemoveCartRequest
import store.msolapps.domain.models.request.RemoveFromFavouritePostModel
import store.msolapps.domain.models.request.UpdateCartPostModel
import store.msolapps.domain.models.response.AddToCartResponseModel
import store.msolapps.domain.models.response.AddToFavourite.AddToFavouriteResponseModel
import store.msolapps.domain.models.response.CartResponseModel
import store.msolapps.domain.useCase.cart.AddProductToCartUseCase
import store.msolapps.domain.useCase.cart.AddProductToFavouriteUseCase
import store.msolapps.domain.useCase.cart.GetCartUseCase
import store.msolapps.domain.useCase.cart.RemoveProductFromCartUseCase
import store.msolapps.domain.useCase.cart.RemoveProductFromFavouriteUseCase
import store.msolapps.domain.useCase.cart.UpdateCartUseCase
import store.msolapps.domain.useCase.general.IsLoggedInUseCase
import store.msolapps.domain.utils.RequestStatus
import store.msolapps.flamingo.util.ConnectionStatus
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(
    private val addToFavouriteUseCase: AddProductToFavouriteUseCase,
    private val addToCartUseCase: AddProductToCartUseCase,
    private val removeProductFromFavouriteUseCase: RemoveProductFromFavouriteUseCase,
    private val removeProductFromCartUseCase: RemoveProductFromCartUseCase,
    private val updateCartUseCase: UpdateCartUseCase,
    private val isLoggedInUseCase: IsLoggedInUseCase,
    private val getCartUseCase: GetCartUseCase,
    private val psp: ProfileSharedPreference,
    private val isOnline: ConnectionStatus,
    private val isLoggedIn: IsLoggedInUseCase,
) : ViewModel() {
    //add to favourite
    private val _addProductToFavouriteData = MutableLiveData<AddToFavouriteResponseModel?>()
    private val _addProductToFavouriteError = MutableLiveData<String>()
    private val _addProductToFavouriteLoading = MutableLiveData<Boolean>(false)
    val addProductToFavouriteData: LiveData<AddToFavouriteResponseModel?> get() = _addProductToFavouriteData
    val addProductToFavouriteError: LiveData<String> get() = _addProductToFavouriteError
    val addProductToFavouriteLoading: LiveData<Boolean> get() = _addProductToFavouriteLoading

    //add to cart
    private val _addProductToCartData = MutableLiveData<AddToCartResponseModel?>()
    private val _addProductToCartError = MutableLiveData<String>()
    private val _addProductToCartLoading = MutableLiveData<Boolean>(false)
    val addProductToCartData: LiveData<AddToCartResponseModel?> get() = _addProductToCartData
    val addProductToCartError: LiveData<String> get() = _addProductToCartError
    val addProductToCartLoading: LiveData<Boolean> get() = _addProductToCartLoading

    //remove from cart
    private val _removeProductFromCartData = MutableLiveData<AddToCartResponseModel?>()
    private val _removeProductFromCartError = MutableLiveData<String>()
    private val _removeProductFromCartLoading = MutableLiveData<Boolean>(false)
    val removeProductFromCartData: LiveData<AddToCartResponseModel?> get() = _removeProductFromCartData
    val removeProductFromCartError: LiveData<String> get() = _removeProductFromCartError
    val removeProductFromCartLoading: LiveData<Boolean> get() = _removeProductFromCartLoading

    //remove from favourite
    private val _removeProductFromFavouriteData = MutableLiveData<AddToFavouriteResponseModel?>()
    private val _removeProductFromFavouriteError = MutableLiveData<String>()
    private val _removeProductFromFavouriteLoading = MutableLiveData<Boolean>(false)
    val removeProductFromFavouriteData: LiveData<AddToFavouriteResponseModel?> get() = _removeProductFromFavouriteData
    val removeProductFromFavouriteError: LiveData<String> get() = _removeProductFromFavouriteError
    val removeProductFromFavouriteLoading: LiveData<Boolean> get() = _removeProductFromFavouriteLoading

    //get cart
    private val _isLoadCart = MutableLiveData<Boolean>(false)
    private val _cart = MutableLiveData<CartResponseModel?>()
    private val _errorCart = MutableLiveData<String>()
    val isLoadCart: LiveData<Boolean> get() = _isLoadCart
    val cart: LiveData<CartResponseModel?> get() = _cart
    val errorCart: LiveData<String> get() = _errorCart

    // update product
    private val _isLoadUpdate = MutableLiveData<Boolean>(false)
    private val _updateProduct = MutableLiveData<UpdateCartPostModel?>()
    private val _errorUpdateProduct = MutableLiveData<String>()
    val isLoadUpdate: LiveData<Boolean> get() = _isLoadUpdate
    val updateProduct: LiveData<UpdateCartPostModel?> get() = _updateProduct
    val errorUpdateProduct: LiveData<String> get() = _errorUpdateProduct
    /* //remove from cart
     private val _addProductToCartData = MutableLiveData<AddToCartResponseModel>()
     private val _addProductToCartError = MutableLiveData<String>()
     private val _addProductToCartLoading = MutableLiveData<Boolean>(false)
     val getHomeBannersData: LiveData<AddToCartResponseModel> get() = _addProductToCartData
     val getHomeBannersError: LiveData<String> get() = _addProductToCartError
     val getHomeBannersLoading: LiveData<Boolean> get() = _addProductToCartLoading
 */

    fun addProductToFavourite(product_id: String) {
        viewModelScope.launch {
            addToFavouriteUseCase.invoke(
                product_id = product_id
            ).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _addProductToFavouriteData.postValue(requestStatus.data)
                        _addProductToFavouriteLoading.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _addProductToFavouriteError.postValue(requestStatus.message)
                        _addProductToFavouriteLoading.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _addProductToFavouriteLoading.postValue(true)
                    }
                }
            }
        }
    }

    fun addProductToCart(productId: String, qty: Int) {
        val addToCartPostModel = AddToCartPostModel(productId, qty, store_id = psp.getStoreId())
        viewModelScope.launch {
            addToCartUseCase.invoke(addToCartPostModel).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _addProductToCartData.postValue(requestStatus.data)
                        _addProductToCartLoading.postValue(false)
                        getCartData()
                    }

                    is RequestStatus.Error -> {
                        _addProductToCartError.postValue(requestStatus.message)
                        _addProductToCartLoading.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _addProductToCartLoading.postValue(true)
                    }
                }
            }
        }
    }

    fun removeProductFromFavourite(productId: String) {
        val removeFromFavouritePostModel =
            RemoveFromFavouritePostModel(product_id = mutableListOf(productId))
        viewModelScope.launch {
            removeProductFromFavouriteUseCase.invoke(removeFromFavouritePostModel)
                .collect { requestStatus ->
                    when (requestStatus) {
                        is RequestStatus.Success -> {
                            _removeProductFromFavouriteData.postValue(requestStatus.data)
                            _removeProductFromFavouriteLoading.postValue(false)
                        }

                        is RequestStatus.Error -> {
                            _removeProductFromFavouriteError.postValue(requestStatus.message)
                            _removeProductFromFavouriteLoading.postValue(false)
                        }

                        is RequestStatus.Waiting -> {
                            _removeProductFromFavouriteLoading.postValue(true)
                        }
                    }
                }
        }
    }

    fun removeProductFromCart(productId: String) {
        val addToCartPostModel = RemoveCartRequest(mutableListOf(productId.toInt()))
        viewModelScope.launch {
            removeProductFromCartUseCase.invoke(addToCartPostModel).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _removeProductFromCartData.postValue(requestStatus.data)
                        _removeProductFromCartLoading.postValue(false)
                        getCartData()
                    }

                    is RequestStatus.Error -> {
                        _removeProductFromCartError.postValue(requestStatus.message)
                        _removeProductFromCartLoading.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _removeProductFromCartLoading.postValue(true)
                    }
                }
            }
        }
    }
    fun updateProductInCart(productId: String,qty: Int) {
        val updateCartPostModel =
            UpdateCartPostModel(
                psp.getLang(),productId, qty,"1000",
                "01")
        viewModelScope.launch {
            updateCartUseCase.invoke(updateCartPostModel).collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _removeProductFromCartData.postValue(requestStatus.data)
                        _removeProductFromCartLoading.postValue(false)
                        getCartData()
                    }

                    is RequestStatus.Error -> {
                        _removeProductFromCartError.postValue(requestStatus.message)
                        _removeProductFromCartLoading.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _removeProductFromCartLoading.postValue(true)
                    }
                }
            }
        }
    }

    fun getCartData() {
        viewModelScope.launch {
            getCartUseCase.invoke().collect { requestStatus ->
                when (requestStatus) {
                    is RequestStatus.Success -> {
                        _cart.postValue(requestStatus.data)
                        _isLoadCart.postValue(false)
                    }

                    is RequestStatus.Error -> {
                        _errorCart.postValue(requestStatus.message)
                        _isLoadCart.postValue(false)
                    }

                    is RequestStatus.Waiting -> {
                        _isLoadCart.postValue(true)
                    }
                }
            }
        }
    }

    fun clearObservers(){
        _cart.postValue(null)
        _addProductToFavouriteData.postValue(null)
        _removeProductFromCartData.postValue(null)
        _removeProductFromFavouriteData.postValue(null)
        _addProductToCartData.postValue(null)
    }
    fun isOnline() = isOnline.isOnline()
    fun isLoggedIn() = isLoggedIn.invoke()

}