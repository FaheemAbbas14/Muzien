package com.tt.muzien.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tt.muzien.constants.Keys
import com.tt.muzien.databinding.ActivitySplashBinding
import com.tt.muzien.ui.auth.AuthActivity
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.startNewActivity
import com.tt.muzien.utilities.PreferenceManager
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val userPreferences = PreferenceManager.getInstance(this)
        userPreferences.putString(Keys.Access_Token,"Faheem")
        Handler(Looper.getMainLooper()).postDelayed({

            lifecycleScope.launch {
                Log.d("Sample", "Loading Auth Token")
                val authToken = userPreferences.getString(Keys.Access_Token)
                val activity =
                    if (authToken == null) AuthActivity::class.java else HomeActivity::class.java
                startNewActivity(activity)
                finish()
            }


        }, 5000)
    }


}