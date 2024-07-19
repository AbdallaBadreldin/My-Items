package store.msolapps.flamingo.presentation.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import store.msolapps.domain.utils.LanguageHelper
import store.msolapps.flamingo.BaseActivity
import store.msolapps.flamingo.BuildConfig
import store.msolapps.flamingo.R
import store.msolapps.flamingo.databinding.ActivityMainBinding
import store.msolapps.flamingo.presentation.auth.AuthActivity
import store.msolapps.flamingo.presentation.home.checkout.viewmodels.SlotsViewModel
import store.msolapps.flamingo.presentation.home.home.HomeViewModel

@AndroidEntryPoint
class MainActivity : BaseActivity(), NavController.OnDestinationChangedListener {
    private lateinit var binding: ActivityMainBinding
    private var shortAnimationDuration: Int = 300
    private val slotsViewModel: SlotsViewModel by viewModels()
//    private val viewmodel: FilterViewModel by viewModels() //will be used within every product page that got filter
    private val homeViewModel: HomeViewModel by viewModels() //will be used within every product page that got filter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!homeViewModel.isUserLoggedIn()){
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
        //set the Language of the app
        LanguageHelper().setLocale(resources, slotsViewModel.getLanguage())
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.addOnDestinationChangedListener(this)
        navView.itemIconTintList = null
        setupActionBarWithNavController(navController, null)
        navView.setupWithNavController(navController)
    }

    private fun isAppUpdated() {
        val localVersionCode = BuildConfig.VERSION_CODE.toLong()

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val remoteVersionCode = remoteConfig.getLong("androidVersionCode")
                    if (remoteVersionCode > localVersionCode) {
                        Toast.makeText(this, getString(R.string.need_update), Toast.LENGTH_LONG)
                            .show()
                        val packageName =
                            this.javaClass.getPackage()?.name ?: "store.msolapps.flamingo"
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.data = Uri.parse("market://details?id=$packageName")
                        startActivity(intent)
                    }
                }
            }
    }

    override fun showLoading() {
        binding.loadingContainer.visibility = View.VISIBLE
        binding.loadingIcon.visibility = View.VISIBLE
        Glide.with(this).asGif().load(R.raw.loading).into(binding.loadingIcon)
        binding.root.isUserInteractionEnabled(false)
    }

    override fun hideLoading() {
        binding.loadingContainer.visibility = View.GONE
        binding.loadingIcon.visibility = View.GONE
        binding.root.isUserInteractionEnabled(true)
    }

    override fun onDestinationChanged(
        controller: NavController, destination: NavDestination, arguments: Bundle?
    ) {
        hideAllWaves()
        showNavBar()
        when (destination.label) {
            getString(R.string.home) -> {
                showWave1()
            }

            getString(R.string.category) -> {
                showWave2()
            }

            getString(R.string.offers) -> {
                showWave3()
            }

            getString(R.string.profile) -> {
                showWave4()
            }

            getString(R.string.cart), getString(R.string.show_product_fragment),
            getString(R.string.search_frag), getString(R.string.settings_frag),
            getString(R.string.personal_frag), getString(R.string.checkoutfragment),
            getString(R.string.addAddressFragment), getString(R.string.mapFragment),
            getString(R.string.my_address_frag), getString(R.string.edit_address_frag),
            getString(R.string.my_orders_frag), getString(R.string.my_order_details),
            getString(R.string.WishlistFragment), getString(R.string.WishlistFragment),
            getString(R.string.ShowNormalProductFragment), getString(R.string.showsinglecategorysuggestionfragment),
            getString(
                R.string.fragment_checkout_success
            ),
            getString(R.string.show_single_category), getString(R.string.show_offers),
            getString(R.string.show_category), getString(R.string.show_multi_category),
            getString(R.string.show_multi_products_category), getString(R.string.show__products_from_home), getString(
                R.string.checkoutaddressbottomsheet
            ),
            getString(R.string.iframe_web_fragment), getString(R.string.SearchFragmentFromHomeFragment), getString(
                R.string.fragment_slots_bottom_sheet
            ),
            getString(R.string.Order_Details_From_Checkout_Fragment),
            getString(R.string.CategoryFromHomeFragment)
            -> {
                hideNavBar()
            }
        }
    }

    private fun showNavBar() {
        binding.navCard.visibility = View.VISIBLE
    }

    private fun hideNavBar() {
        binding.navCard.visibility = View.GONE
    }

    private fun showWave1() {
        showCrossFadeAnimation(binding.wave1)
    }

    private fun showWave2() {
        showCrossFadeAnimation(binding.wave2)
    }

    private fun showWave3() {
        showCrossFadeAnimation(binding.wave3)
    }

    private fun showWave4() {
        showCrossFadeAnimation(binding.wave4)
    }

    private fun hideAllWaves() {
        if (binding.wave1.visibility == View.VISIBLE)
            hideCrossFadeAnimation(binding.wave1)
        if (binding.wave2.visibility == View.VISIBLE)
            hideCrossFadeAnimation(binding.wave2)
        if (binding.wave3.visibility == View.VISIBLE)
            hideCrossFadeAnimation(binding.wave3)
        if (binding.wave4.visibility == View.VISIBLE)
            hideCrossFadeAnimation(binding.wave4)
    }

    private fun showCrossFadeAnimation(imageViewToShow: ImageView) {
        imageViewToShow.apply {
            // Set the content view to 0% opacity but visible, so that it is
            // visible but fully transparent during the animation.
            alpha = 0f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity and clear any animation
            // listener set on the view.
            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }
    }

    private fun hideCrossFadeAnimation(imageViewToHide: ImageView) {
        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step so it doesn't
        // participate in layout passes.
        imageViewToHide.animate()
            .alpha(0f)
            .setDuration(shortAnimationDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
//                    imageViewToHide.visibility = View.INVISIBLE
                }
            })
    }

    override fun onResume() {
        super.onResume()
        isAppUpdated()
    }
}