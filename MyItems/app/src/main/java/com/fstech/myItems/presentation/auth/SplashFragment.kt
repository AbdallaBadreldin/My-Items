package store.msolapps.flamingo.presentation.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.FragmentSplashBinding
import store.msolapps.flamingo.presentation.home.MainActivity


@Suppress("DEPRECATION")
@AndroidEntryPoint
class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        if (viewModel.isFirstOpen()) {
            goToLanguageFragment()
        }
        prepareUi()
//        startShowingVideo()
        startShowingGif()
        return binding.root
    }

    private fun startShowingGif() {

        Glide.with(this).asGif().listener(object : RequestListener<GifDrawable> {

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<GifDrawable>,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: GifDrawable,
                model: Any,
                target: Target<GifDrawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                resource.setLoopCount(1)
                CoroutineScope(Dispatchers.Default).launch {
                    delay(200)
                    while (true) {
                        if (!resource.isRunning) {
                            withContext(Dispatchers.Main) {
                                if (viewModel.isLoggedIn())
                                    goToHomeScreen()
                                else
                                    goToWelcomeFragment() //do your stuff
                            }
                            break
                        }
                    }
                }.start()
                return false
            }

        }).load(R.drawable.welcome_design).into(binding.gifImage)
    }

    private fun goToLanguageFragment() {
        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToSelectLanguageFragment())
    }

    private fun goToHomeScreen() {
        requireActivity().startActivity(Intent(context, MainActivity::class.java))
        requireActivity().finishAffinity()
    }

    private fun prepareUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController!!.hide(android.R.style.Theme_NoTitleBar_Fullscreen)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun goToWelcomeFragment() {
        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToWelcomeFragment())
        removeFullScreen()
    }

    private fun removeFullScreen() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController!!.show(android.R.style.Theme_NoTitleBar_Fullscreen)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            requireActivity().window.clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}