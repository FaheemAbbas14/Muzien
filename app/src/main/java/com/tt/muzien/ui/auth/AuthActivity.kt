package com.tt.muzien.ui.auth

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tt.muzien.R
import com.tt.muzien.constants.Keys
import com.tt.muzien.data.dto.LoggedInInfo
import com.tt.muzien.databinding.ActivityAuthBinding
import com.tt.muzien.ui.onboarding.FragmentWelcome
import com.tt.muzien.ui.views.CustomLoadingIndicator
import com.tt.muzien.utilities.FragmentManager
import com.tt.muzien.utilities.PreferenceManager

/**
 * Provides User Authentication screens
 */
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var customLoadingIndicator: CustomLoadingIndicator

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        changeStatusBarColor(R.color.colorPrimary)
        customLoadingIndicator = CustomLoadingIndicator(this, Color.WHITE)
        var tutorialShown =
            PreferenceManager.getInstance(this).getBoolean(Keys.Tutorial_Shown, false)
        if (tutorialShown) {
            if (LoggedInInfo.userId > 0 && LoggedInInfo.user != null && LoggedInInfo.user!!.fullName == null) {
                var nextFragment=FragmentSignup()
                nextFragment.closeApp=true
                loadFragment(nextFragment)
            } else {
                loadFragment(FragmentSignIn())
            }
        } else {

            loadFragment(FragmentWelcome())

        }

    }

    fun changeBackground(color: Int) {
        binding.mainLayout.setBackgroundColor(color)
    }

    fun showLoadingIndicator() {
        // Show the loading indicator
        customLoadingIndicator.show()
    }

    fun hideLoadingIndicator() {
        // Show the loading indicator
        customLoadingIndicator.dismiss()
    }

    fun loadFragment(newFragment: Fragment) {
        FragmentManager().loadFragment(newFragment, supportFragmentManager)
    }

    fun popFragment() {
        FragmentManager().popFragment(supportFragmentManager)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun changeStatusBarColor(colorResId: Int) {
        window.statusBarColor = resources.getColor(colorResId, theme)
        if (colorResId == R.color.white) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = 0

        }
    }
    fun setStatusBarIconColor(window: Window, isLightBackground: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above
            window.insetsController?.setSystemBarsAppearance(
                if (isLightBackground) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            // For Android 6.0 to 10
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = if (isLightBackground) {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                0
            }
        }
    }


}