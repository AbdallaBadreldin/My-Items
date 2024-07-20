package com.fstech.myItems.presentation.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.fstech.myItems.R
import com.fstech.myItems.databinding.FragmentSecondBinding
import com.google.android.material.snackbar.Snackbar
import com.jetawy.domain.utils.AuthState

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
        /*  binding.buttonSecond.setOnClickListener {
              findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
          }*/
    }

    private fun setupListeners() {
        binding.pinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val code = s.toString()
                if (code.length != 6) {
                    return
                }
                viewModel.verifyCode(code)
            }
        })

        binding.buttonFirst.setOnClickListener {
            val code = binding.pinView.text.toString()
            if (code.length == 6)
                viewModel.verifyCode(code = binding.pinView.text.toString())
            else
                Toast.makeText(requireContext(), "Enter 6 digit code", Toast.LENGTH_SHORT).show()
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
                    findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                    viewModel.codeSent.removeObservers(viewLifecycleOwner)
                }

                is AuthState.Loading -> {
                    binding.buttonFirst.visibility = View.INVISIBLE
                    binding.buttonFirst.isEnabled = false
                }

                is AuthState.OnCodeSent -> {
                    binding.buttonFirst.visibility = View.VISIBLE
                    binding.buttonFirst.isEnabled = true
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