package com.tt.muzien.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import com.tt.muzien.data.network.MemberApi
import com.tt.muzien.data.network.RemoteDataSource
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.SaloonApi
import com.tt.muzien.data.network.ServiceApi
import com.tt.muzien.data.network.UserApi
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.data.repository.MemberRepository
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.data.repository.ServiceRepository
import com.tt.muzien.data.repository.UserRepository
import com.tt.muzien.databinding.ActivityHomeBinding
import com.tt.muzien.ui.auth.AuthActivity
import com.tt.muzien.ui.auth.AuthViewModel
import com.tt.muzien.ui.bookings.FragmentBookings
import com.tt.muzien.ui.bottomSheets.AddBottomSheet
import com.tt.muzien.ui.notifications.FragmentNotifications
import com.tt.muzien.ui.profile.FragmentProfile
import com.tt.muzien.ui.saloon.FragmentAddSaloon
import com.tt.muzien.ui.saloon.SaloonViewModel
import com.tt.muzien.ui.saloon.tabs.FragmentAddSaloonMember
import com.tt.muzien.ui.saloon.tabs.FragmentAddSaloonServices
import com.tt.muzien.ui.startNewActivity
import com.tt.muzien.ui.views.CustomLoadingIndicator
import com.tt.muzien.utilities.FontScaleContextWrapper
import com.tt.muzien.utilities.FragmentNavigator
import com.tt.muzien.utilities.LocaleHelper
import com.tt.muzien.utilities.PreferenceManager

/**
 * Holds logged-in user experience.
 */
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var customLoadingIndicator: CustomLoadingIndicator
    var selectedTab: Int = R.id.rdoAnalytics
    var assignedSaloon: SaloonDto? = null
    var isServiceProvider = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //  LoggedInInfo.user?.role = "salon-manager"
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor(Color.TRANSPARENT)
        setSystemWindow(true)
        setStatusBarIconColor(window, true)
        customLoadingIndicator = CustomLoadingIndicator(this, Color.WHITE)
        setUserData()
        // Default fragment
        if (LoggedInInfo.user?.role == "salon-manager") {
            isServiceProvider = false
            getSaloons()

        } else if (LoggedInInfo.user?.role == "service-provider") {
            isServiceProvider = true
            getSaloons()


        } else {
            loadFragment(HomeFragment())

            binding.llHome.setOnClickListener {
                binding.homeBg.visibility = View.VISIBLE
                binding.bookingBg.visibility = View.INVISIBLE
                binding.homeIcon.setImageDrawable(resources.getDrawable(R.drawable.home_selected))
                binding.homeTitle.setTextColor(resources.getColor(R.color.colorPrimary))
                binding.bookingIcon.setImageDrawable(resources.getDrawable(R.drawable.booking_unselected))
                binding.bookingTitle.setTextColor(resources.getColor(R.color.colorTextLabelDefault))
//                for (i in 0 until supportFragmentManager.backStackEntryCount - 1) {
//                    popFragment()
//                }
                loadFragment(HomeFragment())
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

            ViewCompat.setOnApplyWindowInsetsListener(binding.llMainView) { v, insets ->
                val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                // Adjust the image padding if needed
                v.setPadding(0, 0, 0, systemBarsInsets.bottom)
                insets
            }
        }

        binding.constraintLayout2.post {
            binding.constraintLayout2.setLayerType(View.LAYER_TYPE_SOFTWARE, null) // for shadow
            binding.constraintLayout2.background = createSvgBackgroundWithSmallShadow(
                this,
                binding.constraintLayout2.width,
                binding.constraintLayout2.height
            )
        }
    }

    fun setUserData() {
        binding.imgNotifications.setOnClickListener {
            loadFragment(FragmentNotifications())
        }
        binding.imgProfile.setOnClickListener {
            loadFragment(FragmentProfile())
        }
        binding.imgProfilePic.setOnClickListener {
            loadFragment(FragmentProfile())
        }
        val firstName = LoggedInInfo.user?.fullName
            ?.split(" ")
            ?.firstOrNull()
            ?: ""
        binding.txtUserName.text = "Welcome ${firstName}"
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
        val languageUpdatedContext =
            LocaleHelper.setLocale(base, PreferenceManager.getInstance(base).getLanguage())
        val fontSafeContext = FontScaleContextWrapper.wrap(languageUpdatedContext)
        super.attachBaseContext(fontSafeContext)
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
        FragmentNavigator().loadFragment(newFragment, supportFragmentManager)
    }

    fun loadFragment(
        fragment: Fragment,
        container: Int,
    ) {
        FragmentNavigator()
            .loadFragment(fragment, supportFragmentManager, container)
    }

    fun showTopBar() {
        binding.constraintLayout.visibility = View.VISIBLE
    }

    fun showTabs() {
        binding.constraintLayout.visibility = View.VISIBLE
        if (LoggedInInfo.user?.role == "business-owner") {
            binding.constraintLayout2.visibility = View.VISIBLE
            binding.imgadd.visibility = View.VISIBLE
        } else {
            binding.constraintLayout2.visibility = View.GONE
            binding.imgadd.visibility = View.GONE
        }
    }

    fun hideTabs() {
        binding.constraintLayout2.visibility = View.GONE
        binding.constraintLayout.visibility = View.GONE
        binding.imgadd.visibility = View.GONE

    }

    fun invisibleTabs() {
        binding.constraintLayout2.visibility = View.GONE
        binding.constraintLayout.visibility = View.INVISIBLE
        binding.imgadd.visibility = View.GONE

    }

    fun popFragment() {
        FragmentNavigator().popFragment(supportFragmentManager)
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

    fun getMemberRepo(): MemberRepository {
        var remoteDataSource = RemoteDataSource()
        return MemberRepository(
            remoteDataSource.buildApi(MemberApi::class.java, this)
        )
    }

    fun getServiceRepo(): ServiceRepository {
        var remoteDataSource = RemoteDataSource()
        return ServiceRepository(
            remoteDataSource.buildApi(ServiceApi::class.java, this)
        )
    }

    fun getUserData() {
        var accessToken = PreferenceManager.getInstance(this).getString(Keys.Access_Token)
        var user = PreferenceManager.getInstance(this).getString(Keys.User, defaultValue = "")
        if (accessToken != null && accessToken != "" && user != ""
        ) {
            LoggedInInfo.user = Gson().fromJson(
                user,
                com.tt.muzien.data.responses.UserData::class.java
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
                        LoggedInInfo.user = it.value.data
                        val gson = Gson()
                        val userInfo = gson.toJson(it.value.data)
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

    private fun getSaloons() {
        var remoteDataSource = RemoteDataSource()
        var saloonRepository = SaloonRepository(
            remoteDataSource.buildApi(SaloonApi::class.java, this)
        )
        var saloonViewModel = SaloonViewModel(saloonRepository)
        saloonViewModel.getSaloon.observe(this) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    hideLoadingIndicator()
                    if (it.value.status != 0) {

                        for (saloon in it.value.data.items) {

                            assignedSaloon = SaloonDto(
                                saloon.id.toInt(),
                                saloon.SaloonImages,
                                saloon.name,
                                if (saloon.status == "open") true else false,
                                saloon.isActive,
                                saloon.address ?: "",
                                "${saloon.tRating} (${saloon.numReviews} ${
                                    if (saloon.numReviews.toInt() == 1) "review" else "reviews"
                                })",
                                saloon.SaloonWorkHours,
                                saloon.locationLat.toDouble(), saloon.locationLong.toDouble()
                            )


                        }
                        checkSaloon()
                    }
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())

                }

                else -> {}
            }
        }
        saloonViewModel.getSaloons(page = 1)
        showLoadingIndicator()
    }

    private fun checkSaloon() {
        if (isServiceProvider) {
            var nextFragment = ServiceProviderDashboard()
            nextFragment.saloonId = if (assignedSaloon!=null) assignedSaloon!!.id.toInt() else null
            loadFragment(nextFragment)
        } else {
            var nextFragment = SaloonManagerDashboard()
            nextFragment.selectedSaloon = if (assignedSaloon!=null) assignedSaloon else null
            loadFragment(nextFragment)
            binding.constraintLayout2.visibility = View.GONE
            binding.imgadd.visibility = View.GONE
        }

    }

    fun createSvgBackgroundWithSmallShadow(
        context: Context,
        width: Int,
        height: Int,
        bottomPaddingDp: Float = 1f, // 1 dp padding and shadow offset
    ): Drawable {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            // Shadow with 1dp vertical offset and a smaller blur radius
            setShadowLayer(
                8f,
                0f,
                bottomPaddingDp * context.resources.displayMetrics.density,
                Color.parseColor("#22000000")
            )
        }

        val path = Path()

        val scaleX = width / 360f
        val scaleY = height / 60f

        // Convert dp padding to px, then to viewport coords
        val density = context.resources.displayMetrics.density
        val bottomPaddingPx = bottomPaddingDp * density
        val paddingInViewport = bottomPaddingPx / scaleY

        fun shiftY(y: Float) = (y - paddingInViewport).coerceAtLeast(0f) * scaleY

        path.moveTo(180f * scaleX, shiftY(18.75f))
        path.cubicTo(
            159.28f * scaleX, shiftY(18.75f),
            138.005f * scaleX, shiftY(0f),
            117.285f * scaleX, shiftY(0f)
        )
        path.lineTo(45f * scaleX, shiftY(0f))
        path.cubicTo(
            28.4315f * scaleX, shiftY(0f),
            15f * scaleX, shiftY(13.4315f),
            15f * scaleX, shiftY(30f)
        )
        path.cubicTo(
            15f * scaleX, shiftY(46.5685f),
            28.4315f * scaleX, shiftY(60f),
            45f * scaleX, shiftY(60f)
        )
        path.lineTo(315f * scaleX, shiftY(60f))
        path.cubicTo(
            331.569f * scaleX, shiftY(60f),
            345f * scaleX, shiftY(46.5685f),
            345f * scaleX, shiftY(30f)
        )
        path.cubicTo(
            345f * scaleX, shiftY(13.4315f),
            331.569f * scaleX, shiftY(0f),
            315f * scaleX, shiftY(0f)
        )
        path.lineTo(242.715f * scaleX, shiftY(0f))
        path.cubicTo(
            221.995f * scaleX, shiftY(0f),
            200.72f * scaleX, shiftY(18.75f),
            180f * scaleX, shiftY(18.75f)
        )
        path.close()

        canvas.drawPath(path, paint)
        return BitmapDrawable(context.resources, bitmap)
    }


}