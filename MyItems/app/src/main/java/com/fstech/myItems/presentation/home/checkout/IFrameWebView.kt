package store.msolapps.flamingo.presentation.home.checkout

import android.content.DialogInterface
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import store.msolapps.domain.models.request.CheckoutPostModel
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentPaymobWebViewBinding
import store.msolapps.flamingo.presentation.home.checkout.viewmodels.CheckoutViewModel


@AndroidEntryPoint
class IFrameWebView : BaseFragment() {
    private var _binding: FragmentPaymobWebViewBinding? = null
    private val binding get() = _binding!!
    val args: IFrameWebViewArgs by navArgs()
    private val viewModel: CheckoutViewModel by activityViewModels()
    lateinit var paymobPostModel: CheckoutPostModel
    var link = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymobWebViewBinding.inflate(inflater, container, false)
        observeData()
        arguments?.let {
            paymobPostModel = it.getSerializable("item") as CheckoutPostModel
            link = it.getString("link").toString()
        }
        // Disable onBack click
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            // With blank your fragment BackPressed will be disabled.
        }
        val root: View = binding.root
        return root
    }

    private fun observeData() {
        viewModel.createOrderData.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            } else {
                if (findNavController().currentDestination?.id != R.id.checkoutSuccessFragment)
                    findNavController().navigate(
                        IFrameWebViewDirections.actionIFrameWebViewToCheckoutSuccessFragment(
                            it.order_id ?: 0
                        )
                    )
                viewModel.clearObserver()
            }
        }
        viewModel.createOrderLoad.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true

        val link = args.link
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(link)
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                super.onReceivedSslError(view, handler, error)
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setMessage(store.msolapps.flamingo.R.string.notification_error_ssl_cert_invalid)
                builder.setPositiveButton("continue",
                    DialogInterface.OnClickListener { dialog, which -> handler?.proceed() })
                builder.setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, which -> handler?.cancel() })
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {
                    Log.e("url", it)
                    if (it.contains("success=true")) {
                        // Handle success
                        viewModel.createOrder(paymobPostModel)
                    } else if (it.contains("success=false")) {
                        // Handle failure
                        lifecycleScope.launch {
                            delay(2000)
                            if (findNavController().currentDestination?.id != R.id.checkoutSuccessFragment)
                                findNavController().navigateUp()
                        }
                    }
                    view?.loadUrl(it)
                }
                return true
            }
        }
    }

    @Override
    fun  onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {

    }
}