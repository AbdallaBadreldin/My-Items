package store.msolapps.flamingo

import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {

    fun Fragment.showLoading() {
        val loadingActions = requireActivity() as LoadingActions
        loadingActions.showLoading()
    }

    fun Fragment.hideLoading() {
        val loadingActions = requireActivity() as LoadingActions
        loadingActions.hideLoading()
    }

}