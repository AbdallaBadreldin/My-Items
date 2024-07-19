package store.msolapps.flamingo.presentation.home.cart.actions

import store.msolapps.domain.models.response.CartResponseModel

interface CartAdapterActions {
    fun addOneProductToCart(item: CartResponseModel.DataCartResponseModel,position:Int)
    fun removeOneProductFromCart(item: CartResponseModel.DataCartResponseModel,position:Int)
    fun removeProductsFromCart(item: CartResponseModel.DataCartResponseModel,position:Int)
}