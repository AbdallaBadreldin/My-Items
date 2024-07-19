package store.msolapps.flamingo.presentation.home.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentProfileBinding
import store.msolapps.flamingo.presentation.auth.AuthActivity

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        viewModel.getDefaultAddress()
        setupObservers()
        setupListeners()
        return binding.root
    }

    private fun setupObservers() {
          viewModel.isLoad.observe(viewLifecycleOwner) {
              if(it!=null){
                  if (it) {
                      showLoading()

                  } else {
                      hideLoading()
                  }
              }
          }
        viewModel.error.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
            }
        }
        viewModel.logout.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.logoutFromProfile()
                requireActivity().startActivity(Intent(context, AuthActivity::class.java))
                requireActivity().finishAffinity()
            }
        }
        viewModel.getAddressData.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.addressNameTv.setText(it.data!!.name)
            }
        }
        viewModel.getAddressLoading.observe(viewLifecycleOwner) {
            if(it!=null){
                if (it) {
                    showLoading()

                } else {
                    hideLoading()
                }
            }
        }
    }

    fun setupListeners() {
        binding.signoutTv.setOnClickListener {
            viewModel.logout()
        }
        binding.settings.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_settingsFragment)
        }
        binding.parentPersonalDetails.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_personalDetailsFragment)
        }
        binding.parentAddress.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToMyAddressesFragment())
        }
        binding.myOrdersTv.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToMyOrderHistory())
        }
        binding.myWishListTv.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToWishlistFragment())
        }
        binding.facebookIcon.setOnClickListener {
            openLinkSocialMedia("https://www.facebook.com/Flamingohypermarket?mibextid=LQQJ4d")
        }
        binding.instagramIcon.setOnClickListener {
                openLinkSocialMedia("https://www.instagram.com/flamingohypermarket?igsh=MTY5ZDR4ZHl0emc3Zg==")
        }
    }

    private fun openLinkSocialMedia(link: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(browserIntent)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearObservers()
    }
}