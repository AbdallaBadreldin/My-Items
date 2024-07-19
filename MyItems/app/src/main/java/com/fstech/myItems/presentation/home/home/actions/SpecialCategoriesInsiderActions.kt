package store.msolapps.flamingo.presentation.home.home.actions

import store.msolapps.domain.models.response.ProductsSpecialCategoriesResponse

interface SpecialCategoriesInsiderActions {
    fun onInnerSpecialCategoriesClicked(position: Int, item: ProductsSpecialCategoriesResponse)
}