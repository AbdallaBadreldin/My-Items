package store.msolapps.flamingo.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.domain.models.request.LoginRequest
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentLoginBinding
import store.msolapps.flamingo.presentation.home.MainActivity

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.isLoad.observe(viewLifecycleOwner) {
            //should show loading progressbar or use paging
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
            Log.d("TAG", it.toString())
        }
        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
        }
        viewModel.login.observe(viewLifecycleOwner) {
            //viewModel.setIsRememberMe(binding.checkBox.isChecked)
            goToHomeActivity()
        }
    }

    private fun goToHomeActivity() {
        requireActivity().startActivity(Intent(context, MainActivity::class.java))
        requireActivity().finishAffinity()
    }

    private fun setupListeners() {
        binding.imageButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.textView6.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections
                    .actionLoginFragmentToForgetPasswordFragment()
            )
        }
        binding.textViewSignUp.setOnClickListener { goToSignup() }
        binding.buttonSignIn.setOnClickListener {
            val email = binding.emailEdit.text.toString()
            val pass = binding.passwordEdit.text.toString()

            if (email.isEmpty())
                showToast(getString(R.string.enter_email))
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                showToast(getString(R.string.enter_valid_email))
            else if (pass.isEmpty())
                showToast(getString(R.string.enter_password))
            else if (pass.length < 6)
                showToast(getString(R.string.the_password_must_be_at_least_6_characters))
            else viewModel.login(LoginRequest(email, pass))
        }
//        binding.emailEdit.doOnTextChanged { text, start, before, count ->
//            binding.emailEdit.error = null
//        }
//        binding.passwordEdit.doOnTextChanged { text, start, before, count ->
//            binding.emailEdit.error = null
//        }
    }

    private fun goToSignup() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}