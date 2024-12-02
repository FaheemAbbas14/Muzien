package com.tt.muzien.ui.auth

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tt.muzien.R
import com.tt.muzien.constants.Keys
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customLoadingIndicator = CustomLoadingIndicator(this, Color.WHITE)
        var tutorialShown =
            PreferenceManager.getInstance(this).getBoolean(Keys.Tutorial_Shown, false)
        if (tutorialShown) {
            loadFragment(FragmentSignIn())
        } else {
            loadFragment(FragmentWelcome())
        }
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


}