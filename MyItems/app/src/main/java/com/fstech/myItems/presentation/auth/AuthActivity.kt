package com.fstech.myItems.presentation.auth

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.fstech.myItems.R
import com.fstech.myItems.base.BaseActivity
import com.fstech.myItems.databinding.ActivityAuthBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.initialize
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAuthBinding
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.initialize(this)
        if (authViewModel.isLoggedIn()) {
            finish()
        }
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideLoading()

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_auth)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_auth)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun showLoading() {
        binding.loadingContainer.visibility = View.VISIBLE
        binding.loadingIcon.visibility = View.VISIBLE
        binding.root.isUserInteractionEnabled(false)
    }

    override fun hideLoading() {
        binding.loadingContainer.visibility = View.GONE
        binding.loadingIcon.visibility = View.GONE
        binding.root.isUserInteractionEnabled(true)
    }
}