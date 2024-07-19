package store.msolapps.flamingo.presentation.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.flamingo.BaseFragment
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.RegisterCodeVerificationBinding
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class RegisterCodeVerification : BaseFragment() {
    private var _binding: RegisterCodeVerificationBinding? = null
    private lateinit var countDownTimer: CountDownTimer
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by activityViewModels()
    private lateinit var phone: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = RegisterCodeVerificationBinding.inflate(inflater, container, false)
        arguments?.let {
            phone = it.getString("phone")!!
        }
        setupUi()
        setupListeners()
        observeData()
        return binding.root
    }

    private fun observeData() {
        viewModel.validateSmsMasrIsLoad.observe(viewLifecycleOwner) {
            if (it == true) showLoading() else hideLoading()
        }
        viewModel.validateSmsMasrData.observe(viewLifecycleOwner) {
            goToCreateAccount()
        }
        viewModel.validateSmsMasrError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun setupUi() {
        binding.editNumber.text = phone
        startTimer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListeners() {
        binding.continueBtn.setOnClickListener {
//            goToCreateAccount()
            val code = binding.verifyCodeEditText.text.toString()
            if (code.length != 6) {
                //should tell user to enter code
                return@setOnClickListener
            }
            viewModel.veriftSmsMasrCode(code)
        }
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.editNumberIcon.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.verifyCodeEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val code = s.toString()
                if (code.length != 6) {
                    return
                }
                viewModel.veriftSmsMasrCode(code)
            }

            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
            }
        })


    }

    private fun goToCreateAccount() {
        findNavController().navigate(RegisterCodeVerificationDirections.actionRegisterCodeVerficationToCreateAccountFragment())
    }

    private fun timer() {
        binding.resendCodeTextView.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.black
            )
        )
//        binding.timerContainer.visibility = View.VISIBLE
        binding.resendCodeTextView.isEnabled = false
        binding.resendCodeTextView.isFocusable = false
        binding.resendCodeTextView.isSelected = true
        countDownTimer = object : CountDownTimer(120000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                //update text
                binding.timeTV.text = getString(
                    R.string.formatted_time,
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                )
            }

            override fun onFinish() {
                binding.resendCodeTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.black
                    )
                )
                binding.resendCodeTextView.isEnabled = true
                binding.resendCodeTextView.isFocusable = true
                binding.resendCodeTextView.isSelected = false
//                binding.timerContainer.visibility = View.GONE

            }
        }
    }

    private fun startTimer() {
        timer()
        countDownTimer.start()
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
    }

    private fun stopTimer() {
        countDownTimer.cancel()
    }
}