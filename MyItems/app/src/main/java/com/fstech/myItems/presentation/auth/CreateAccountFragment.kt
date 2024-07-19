package store.msolapps.flamingo.presentation.auth

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.domain.models.request.SignupRequest
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentCreateAccountBinding


@AndroidEntryPoint
class CreateAccountFragment : BaseFragment() {
    private var _binding: FragmentCreateAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        setColorsOfPrivacy()
        setupListeners()
        setupObservers()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.isLoad.observe(viewLifecycleOwner) {
            //should show loading progressbar or use paging
            if (it == true) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        viewModel.error.observe(viewLifecycleOwner) {
//            Log.d("TAG", it.toString())
            Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
        }
        viewModel.register.observe(viewLifecycleOwner) {
            val bundle = Bundle().apply {
                putString("name", binding.usernameEdit.text.toString())
                putString("email", binding.enterEmailEdit.text.toString())
            }
            findNavController().navigate(
                R.id.action_createAccountFragment_to_addPhotoFragment, bundle
            )
        }
    }


    private fun registerRoutine() {
        val name = binding.usernameEdit.text.toString()
        val email = binding.enterEmailEdit.text.toString()
        val password1 = binding.enterPasswordEdit.text.toString()
        val password2 = binding.confirmPasswordEdit.text.toString()
        if (name.isEmpty()) {
            Toast.makeText(context, getString(R.string.enter_name), Toast.LENGTH_LONG).show()
        } else if (email.isEmpty()) {
            Toast.makeText(context, getString(R.string.enter_email), Toast.LENGTH_LONG).show()
        } else if (password1.isEmpty()) {
            Toast.makeText(context, getString(R.string.enter_password), Toast.LENGTH_LONG).show()
        } else if (password2.isEmpty()) {
            Toast.makeText(
                context, getString(R.string.enter_password_confirmation), Toast.LENGTH_LONG
            ).show()
        } else if (!viewModel.isNameValid(name)) {
            Toast.makeText(
                context, getString(R.string.enter_valid_name), Toast.LENGTH_LONG
            ).show()
        } else if (!viewModel.isEmailValid(email)) {
            Toast.makeText(context, getString(R.string.enter_valid_email), Toast.LENGTH_LONG).show()
        } else if (!viewModel.isPasswordValid(password1)) {
            Toast.makeText(context, getString(R.string.enter_valid_password), Toast.LENGTH_LONG)
                .show()
        } else if (!viewModel.isTwoPasswordIdentical(
                password1, password2
            )
        ) {
            Toast.makeText(
                context,
                getString(R.string.password_and_confirmaion_are_not_similar),
                Toast.LENGTH_LONG
            ).show()
        } else if (!binding.checkBoxRememberMe.isChecked) {
            Toast.makeText(context, getString(R.string.accept_privacy_policy), Toast.LENGTH_LONG)
                .show()
        } else viewModel.register(
            signupRequest = SignupRequest(
                name, viewModel.phone, email, password1, password2
            )
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListeners() {
        binding.signinText.setOnClickListener {
            findNavController().navigate(CreateAccountFragmentDirections.actionCreateAccountFragmentToLoginFragment())
        }
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonSignUp.setOnClickListener {
            registerRoutine()
        }
    }

    private fun setColorsOfPrivacy() {
        val checkBox: AppCompatCheckBox = binding.checkBoxRememberMe
        val text = getString(R.string.agree_with_terms_amp_conditions_and_privacy_policy)
        val spannableString = SpannableString(text)

        // Set "Agree with" to blue color with underline
        val blueColor = ContextCompat.getColor(requireContext(), R.color.black_blue)
        spannableString.setSpan(
            ForegroundColorSpan(blueColor), 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(UnderlineSpan(), 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set "Terms & Conditions" to orange color with underline
        val orangeColor = ContextCompat.getColor(requireContext(), R.color.orange)
        spannableString.setSpan(
            ForegroundColorSpan(orangeColor), 11, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(UnderlineSpan(), 11, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set "Privacy Policy" to blue color with underline
        spannableString.setSpan(
            ForegroundColorSpan(orangeColor),
            33,
            spannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            UnderlineSpan(), 33, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        checkBox.text = spannableString
    }
}