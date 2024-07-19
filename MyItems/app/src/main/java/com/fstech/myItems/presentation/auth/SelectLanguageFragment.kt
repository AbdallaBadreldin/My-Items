package store.msolapps.flamingo.presentation.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.flamingo.databinding.FragmentSelectLanguageBinding


@AndroidEntryPoint
class SelectLanguageFragment : Fragment() {
    private var _binding: FragmentSelectLanguageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnAr.setOnClickListener {
            viewModel.setLanguage("ar")
            viewModel.setAppIsOpened()
            goToSplash()
        }
        binding.btnEn.setOnClickListener {
            viewModel.setLanguage("en")
            viewModel.setAppIsOpened()
            goToSplash()
        }
    }

    private fun goToSplash() {
        findNavController().navigate(SelectLanguageFragmentDirections.actionSelectLanguageFragmentToSplashFragment())
        requireActivity().recreate()
    }

    private var context: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onDetach() {
        super.onDetach()
        this.context = null
    }
}