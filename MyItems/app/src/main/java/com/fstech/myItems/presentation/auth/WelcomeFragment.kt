package store.msolapps.flamingo.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import store.msolapps.flamingo.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        setupListeners()
        prepareUi()
        return binding.root
    }

    private fun prepareUi() {
        requireActivity().window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    private fun setupListeners() {
        binding.getStarted.setOnClickListener {
            goToSignUpFragment()
        }
        binding.signInText.setOnClickListener {
            goToSignInFragment()
        }
    }

    private fun goToSignInFragment() {
        findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment())
        removeFullScreen()
    }

    private fun goToSignUpFragment() {
        findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToRegisterFragment())
        removeFullScreen()
    }

    private fun removeFullScreen(){
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
}