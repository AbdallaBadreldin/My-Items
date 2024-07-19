package store.msolapps.flamingo.presentation.home.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentCheckoutSuccessBinding
import store.msolapps.flamingo.presentation.home.home.HomeViewModel

@AndroidEntryPoint
class CheckoutSuccessFragment : Fragment() {
    private var _binding: FragmentCheckoutSuccessBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private val args by navArgs<CheckoutSuccessFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutSuccessBinding.inflate(inflater, container, false)
        setupUi()
        setupListeners()
        return binding.root
    }

    private fun setupUi() {
        if (homeViewModel.isUserLoggedIn())
            binding.tvUserNameSucc.text = homeViewModel.getUserName()
        else
            binding.tvUserNameSucc.text = getString(
                R.string.guest
            )
    }

    private fun setupListeners() {
        binding.backToHomeBtn.setOnClickListener {
            findNavController().navigate(CheckoutSuccessFragmentDirections.actionCheckoutSuccessFragmentToNavigationHome())
        }
        binding.materialCardView.setOnClickListener {
            findNavController().navigate(CheckoutSuccessFragmentDirections.actionCheckoutSuccessFragmentToMOrderHistory())
        }
        binding.buttonViewOrderStatus.setOnClickListener {
            findNavController().navigate(CheckoutSuccessFragmentDirections.actionCheckoutSuccessFragmentToOrderDetailsFromCheckout(args.orderId.toString()))
        }
    }

}