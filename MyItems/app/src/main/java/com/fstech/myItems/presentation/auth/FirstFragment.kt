package com.fstech.myItems.presentation.auth

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.fstech.myItems.R
import com.fstech.myItems.databinding.FragmentFirstBinding
import com.google.android.material.snackbar.Snackbar
import com.jetawy.domain.utils.AuthState
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class FirstFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        binding.textInputEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                // Trigger the button click
                binding.buttonFirst.performClick()
                true // Consume the event
            } else {
                false // Let the system handle other actions
            }
        }
        binding.buttonFirst.setOnClickListener {
            val phone = binding.textInputEdit.text.toString().trim()
            if (phone.isEmpty()) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.enter_phone_number), Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                viewModel.signIn(
                    "+${binding.countryCode.defaultCountryCode}${phone}",
                    Locale.getDefault().language
                )
            }
        }
    }

    private fun setupObservers() {
        viewModel.codeSent.observe(viewLifecycleOwner) {
            when (it) {
                is AuthState.Error -> {
                    Snackbar.make(binding.root, it.error?.message.toString(), Snackbar.LENGTH_SHORT)
                        .show()
                }

                is AuthState.Initial -> {
                    binding.buttonFirst.visibility = View.VISIBLE
                    binding.buttonFirst.isEnabled = true
                }

                is AuthState.Loading -> {
                    binding.buttonFirst.visibility = View.INVISIBLE
                    binding.buttonFirst.isEnabled = false
                }

                is AuthState.OnCodeSent -> {
                    findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                }

                is AuthState.OnSuccess -> {
                    requireActivity().finish()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}