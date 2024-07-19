package store.msolapps.flamingo.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentRegisterBinding
import store.msolapps.flamingo.util.Dialogs
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class RegisterFragment : BaseFragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by activityViewModels()
    private var phone = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        setupListeners()
        setupObservers()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.isLoad.observe(viewLifecycleOwner) {
            if (it == true) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.error.observe(viewLifecycleOwner) {
//            Log.d("TAG", it.toString())
//            viewModel.isPhoneTaken.removeObservers(viewLifecycleOwner)
        }
        viewModel.isPhoneTaken.observe(viewLifecycleOwner) { isTaken ->

            if (isTaken == true) Dialogs(requireContext()).showOkDialog(
                getString(R.string.message), getString(R.string.phone_already_registered)
            )
            else sendSms("2")

        }

        viewModel.SmsMasrData.observe(viewLifecycleOwner) {
            goToVerification()
        }
        viewModel.SmsMasrError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
        }
        viewModel.SmsMasrIsLoad.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
    }

    private fun sendSms(countryCode: String) {
        viewModel.sendSmsMasr("$countryCode${binding.phoneNumberET.text}")
    }

    private fun verifyPhoneNumber() {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber("+20${binding.phoneNumberET.text}") // Phone number to verify
            .setTimeout(120, TimeUnit.SECONDS) // Timeout and unit
            .setCallbacks(this.mCallbacks).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            goToCreateAccount()
        }

        override fun onVerificationFailed(e: FirebaseException) {
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    // Invalid request
                }

                is FirebaseTooManyRequestsException -> {
                    // The SMS quota for the project has been exceeded
                }

                is FirebaseAuthMissingActivityForRecaptchaException -> {
                    // reCAPTCHA verification attempted with null Activity
                }
            }
            Toast.makeText(context, e.localizedMessage?.toString() ?: "", Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(
            verificationId: String, token: PhoneAuthProvider.ForceResendingToken
        ) {
//            viewModel.verificationId = verificationId
//            viewModel.token = token
//            viewModel.phone = binding.phoneNumberET.text.toString()
            goToVerification()
        }
    }

    private fun goToCreateAccount() {
        findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToCreateAccountFragment())
        viewModel.resetLiveData()
    }

    private fun setupListeners() {
        binding.continueBtn.setOnClickListener {
             phone = binding.phoneNumberET.text.toString()
            if (viewModel.isPhoneValid(phone = phone)) {
                viewModel.checkIfPhoneTaken(phone)
            } else {
                Dialogs(requireContext()).showOkDialog(
                    getString(R.string.message), getString(R.string.enter_valid_phone)
                )
            }
        }
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun goToVerification() {
        val bundle = Bundle().apply {
            viewModel.phone = phone
            putString("phone", phone)
        }
        findNavController().navigate(
            R.id.action_registerFragment_to_registerCodeVerfication, bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}