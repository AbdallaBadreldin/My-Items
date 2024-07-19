package store.msolapps.flamingo.presentation.auth

import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentForgetPasswordBinding
import store.msolapps.flamingo.util.Dialogs

@AndroidEntryPoint
class ForgetPasswordFragment : Fragment() {

    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ForgetPasswordViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        setupListeners()
        setupObservers()
        return binding.root
    }

    private fun setupListeners() {
        binding.continueBtn.setOnClickListener {
            viewModel.forgetPassword(binding.emailEdit.text.toString())
            binding.layout.visibility = View.GONE
            binding.layout2.visibility = View.VISIBLE
            replaceEmail(binding.emailEdit.text.toString())
        }
        binding.tryAgain.setOnClickListener {
            binding.layout.visibility = View.VISIBLE
            binding.layout2.visibility = View.GONE
        }
        binding.signin.setOnClickListener {
            findNavController().navigate(ForgetPasswordFragmentDirections.actionForgetPasswordFragmentToLoginFragment())
        }
    }

    private fun replaceEmail(newEmail: String) {
        val fullText =
            getString(R.string.click_the_link_sent_to_m_m_gmail_com_before_reset_your_password)
        val replacedText = fullText.replace("M***********m@gmail.com", newEmail)

        val spannableString = SpannableString(replacedText)

        val textView = binding.textView4
        textView.text = spannableString
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        viewModel.isLoad.observe(viewLifecycleOwner) {
            //should show loading progressbar or use paging
            Log.d("TAG", it.toString())
        }
        viewModel.error.observe(viewLifecycleOwner) {
            Log.d("TAG", it.toString())
            viewModel.forgetPassword.removeObservers(viewLifecycleOwner)
        }
        viewModel.forgetPassword.observe(viewLifecycleOwner) {
            Log.d("TAG", it.toString())
        }
    }
}