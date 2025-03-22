package com.tt.muzien.ui.home

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.tt.muzien.R
import com.tt.muzien.constants.Keys
import com.tt.muzien.data.dto.LoggedInInfo
import com.tt.muzien.data.dto.SaloonDto
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.network.RemoteDataSource
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.SaloonApi
import com.tt.muzien.data.network.UserApi
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.data.repository.UserRepository
import com.tt.muzien.databinding.ActivityHomeBinding
import com.tt.muzien.ui.auth.AuthActivity
import com.tt.muzien.ui.auth.AuthViewModel
import com.tt.muzien.ui.bookings.FragmentBookings
import com.tt.muzien.ui.bottomSheets.AddBottomSheet
import com.tt.muzien.ui.notifications.FragmentNotifications
import com.tt.muzien.ui.profile.FragmentProfile
import com.tt.muzien.ui.saloon.FragmentAddSaloon
import com.tt.muzien.ui.saloon.tabs.FragmentAddSaloonMember
import com.tt.muzien.ui.saloon.tabs.FragmentAddSaloonServices
import com.tt.muzien.ui.startNewActivity
import com.tt.muzien.ui.views.CustomLoadingIndicator
import com.tt.muzien.utilities.FragmentManager
import com.tt.muzien.utilities.LocaleHelper
import com.tt.muzien.utilities.PreferenceManager

/**
 * Holds logged-in user experience.
 */
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var customLoadingIndicator: CustomLoadingIndicator
    var selectedTab: Int = R.id.rdoAnalytics

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(Color.TRANSPARENT)
        setSystemWindow(true)
        setStatusBarIconColor(window, true)
        customLoadingIndicator = CustomLoadingIndicator(this, Color.WHITE)
        setUserData()
        // Default fragment
        if (LoggedInInfo.user?.role == "salon-manager") {
            var nextFragment = SaloonManagerDashboard()
            nextFragment.selectedSaloon = SaloonDto(
                0,
                listOf(),
                "The Style Zone",
                true,
                "Rd. 2121 Alamal Dist. 12643 Riyadh SA",
                "4.5 (2398 reviews)",
                listOf()
            )
            loadFragment(nextFragment)
            binding.constraintLayout2.visibility = View.GONE
            binding.imgadd.visibility = View.GONE

        } else {
            loadFragment(HomeFragment())
        }
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

            when (selectedTab) {

                R.id.rdoSaloons -> {
                    var nextFragment = FragmentAddSaloon()
                    loadFragment(nextFragment)
                }

                R.id.rdoMembers -> {
                    var nextFragment = FragmentAddSaloonMember()
                    nextFragment.fromMain = true
                    loadFragment(nextFragment)
                }

                R.id.rdoServices -> {
                    var nextFragment = FragmentAddSaloonServices()
                    loadFragment(nextFragment)
                }

                else -> {
                    val bottomSheet = AddBottomSheet(this)
                    bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                }
            }

        }
        binding.imgNotifications.setOnClickListener {
            loadFragment(FragmentNotifications())
        }
        binding.imgProfile.setOnClickListener {
            loadFragment(FragmentProfile())
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.llMainView) { v, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Adjust the image padding if needed
            v.setPadding(0, 0, 0, systemBarsInsets.bottom)
            insets
        }
    }

    fun setUserData() {
        binding.txtUserName.text = "Welcome ${LoggedInInfo.user?.fullName}"
        binding.txtRole.text = "${LoggedInInfo.user?.role}"
        Glide.with(binding.imgProfilePic)
            .load(LoggedInInfo.user?.picture)
            .circleCrop()
            .placeholder(R.drawable.user_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // Cache both original & transformed image
            .skipMemoryCache(false)  // Cache in memory
            .into(binding.imgProfilePic)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(
            LocaleHelper.setLocale(
                base,
                PreferenceManager.getInstance(base).getLanguage()
            )
        )
    }

    fun setSelectedTab(selected: Int?) {
        if (selected != null) {
            selectedTab = selected
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

    fun loadFragment(
        fragment: Fragment,
        container: Int,
    ) {
        FragmentManager()
            .loadFragment(fragment, supportFragmentManager, container)
    }

    fun showTabs() {
        binding.constraintLayout.visibility = View.VISIBLE
        if (LoggedInInfo.user?.role != "salon-manager") {
            binding.constraintLayout2.visibility = View.VISIBLE
            binding.imgadd.visibility = View.VISIBLE
        }
    }

    fun hideTabs() {
        binding.constraintLayout2.visibility = View.GONE
        binding.constraintLayout.visibility = View.GONE
        binding.imgadd.visibility = View.GONE

    }

    fun popFragment() {
        FragmentManager().popFragment(supportFragmentManager)
    }

    fun setSystemWindow(value: Boolean) {
        if (value) {
            binding.statusBar.visibility = View.VISIBLE
        } else {
            binding.statusBar.visibility = View.GONE
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

    @RequiresApi(Build.VERSION_CODES.M)
    fun changeStatusBarColor(colorResId: Int) {
        window.statusBarColor = colorResId
        if (colorResId == Color.WHITE) {
            binding.statusBar.setBackgroundColor(Color.WHITE)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            if (colorResId == resources.getColor(R.color.colorPrimary)) {
                binding.statusBar.setBackgroundColor(colorResId)
            } else {
                binding.statusBar.setBackgroundColor(Color.WHITE)
            }
            window.decorView.systemUiVisibility = 0

        }
    }

    fun getUserRepo(): UserRepository {
        var remoteDataSource = RemoteDataSource()
        return UserRepository(
            remoteDataSource.buildApi(UserApi::class.java, this)
        )
    }

    fun getSaloonRepo(): SaloonRepository {
        var remoteDataSource = RemoteDataSource()
        return SaloonRepository(
            remoteDataSource.buildApi(SaloonApi::class.java, this)
        )
    }



    fun getUserData() {
        var accessToken = PreferenceManager.getInstance(this).getString(Keys.Access_Token)
        var user = PreferenceManager.getInstance(this).getString(Keys.User, defaultValue = "")
        if (accessToken != null && accessToken != "" && user != ""
        ) {
            LoggedInInfo.user = Gson().fromJson(
                user,
                com.tt.muzien.data.responses.UserInfo::class.java
            )
            LoggedInInfo.userId = LoggedInInfo.user?.id!!
            var remoteDataSource = RemoteDataSource()
            var authRepository = AuthRepository(
                remoteDataSource.buildApi(AuthApi::class.java, this),
                PreferenceManager.getInstance(this)
            )
            var authViewModel = AuthViewModel(authRepository)
            authViewModel.my.observe(this, Observer {
                when (it) {

                    is Resource.Success -> {
                        hideLoadingIndicator()
                        LoggedInInfo.user = it.value.data?.user
                        val gson = Gson()
                        val userInfo = gson.toJson(it.value.data?.user)
                        PreferenceManager.getInstance(this)
                            .putString(Keys.User, userInfo)
                        setUserData()

                    }

                    is Resource.Failure -> {
                        hideLoadingIndicator()
                        Log.d("failed", it.errorBody.toString())
                        loginActivity()
                    }

                    else -> {}
                }
            })

            authViewModel!!.my(LoggedInInfo.userId)
        } else {
            loginActivity()
        }
    }

    fun loginActivity() {
        val activity = AuthActivity::class.java
        startNewActivity(activity)
        finish()
    }
}