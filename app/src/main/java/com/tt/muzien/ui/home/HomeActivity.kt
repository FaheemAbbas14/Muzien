package com.tt.muzien.ui.home

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tt.muzien.R
import com.tt.muzien.databinding.ActivityHomeBinding
import com.tt.muzien.ui.bookings.FragmentBookings
import com.tt.muzien.ui.bottomSheets.AddBottomSheet
import com.tt.muzien.ui.notifications.FragmentNotifications
import com.tt.muzien.ui.profile.FragmentProfile
import com.tt.muzien.ui.views.CustomLoadingIndicator
import com.tt.muzien.utilities.FragmentManager

/**
 * Holds logged-in user experience.
 */
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var customLoadingIndicator: CustomLoadingIndicator

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(R.color.white)
        customLoadingIndicator = CustomLoadingIndicator(this, Color.WHITE)
        // Default fragment
        loadFragment(HomeFragment())
        binding.llHome.setOnClickListener {
            binding.homeBg.visibility = View.VISIBLE
            binding.bookingBg.visibility = View.INVISIBLE
            binding.homeIcon.setImageDrawable(resources.getDrawable(R.drawable.home_selected))
            binding.homeTitle.setTextColor(resources.getColor(R.color.colorPrimary))
            binding.bookingIcon.setImageDrawable(resources.getDrawable(R.drawable.booking_unselected))
            binding.bookingTitle.setTextColor(resources.getColor(R.color.colorTextLabelDefault))
            for (i in 0 until supportFragmentManager.backStackEntryCount - 1) {
                popFragment()
            }
        }
        binding.llBookings.setOnClickListener {
            binding.homeBg.visibility = View.INVISIBLE
            binding.bookingBg.visibility = View.VISIBLE
            binding.homeIcon.setImageDrawable(resources.getDrawable(R.drawable.home_unselected))
            binding.homeTitle.setTextColor(resources.getColor(R.color.colorTextLabelDefault))
            binding.bookingIcon.setImageDrawable(resources.getDrawable(R.drawable.booking_selected))
            binding.bookingTitle.setTextColor(resources.getColor(R.color.colorPrimary))
            loadFragment(FragmentBookings())
        }
        binding.imgadd.setOnClickListener {
            val bottomSheet = AddBottomSheet(this)
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
        binding.imgNotifications.setOnClickListener {
            loadFragment(FragmentNotifications())
        }
        binding.imgProfile.setOnClickListener {
            loadFragment(FragmentProfile())
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

    fun showTabs() {
        binding.constraintLayout2.visibility = View.VISIBLE
        binding.constraintLayout.visibility = View.VISIBLE
        binding.imgadd.visibility = View.VISIBLE
    }

    fun hideTabs() {
        binding.constraintLayout2.visibility = View.GONE
        binding.constraintLayout.visibility = View.GONE
        binding.imgadd.visibility = View.GONE
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