package com.tt.muzien.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.tt.muzien.constants.Keys
import com.tt.muzien.data.dto.LoggedInInfo
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.network.RemoteDataSource
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.databinding.ActivitySplashBinding
import com.tt.muzien.ui.auth.AuthActivity
import com.tt.muzien.ui.auth.AuthViewModel
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.startNewActivity
import com.tt.muzien.utilities.FontScaleContextWrapper
import com.tt.muzien.utilities.LocaleHelper
import com.tt.muzien.utilities.PreferenceManager
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var authViewModel: AuthViewModel? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val userPreferences = PreferenceManager.getInstance(this)
//        userPreferences.putString(Keys.Access_Token,"Faheem")
        Handler(Looper.getMainLooper()).postDelayed({

            lifecycleScope.launch {
                getUserData()
            }


        }, 5000)
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
            if (LoggedInInfo.user != null) {
                LoggedInInfo.userId = LoggedInInfo.user?.id!!
                var remoteDataSource = RemoteDataSource()
                var authRepository = AuthRepository(
                    remoteDataSource.buildApi(AuthApi::class.java, this),
                    PreferenceManager.getInstance(this)
                )
                authViewModel = AuthViewModel(authRepository)
                authViewModel!!.my.observe(this, Observer {
                    when (it) {
                        is Resource.Success -> {
                            LoggedInInfo.user = it.value.data
                            val gson = Gson()
                            val userInfo = gson.toJson(it.value.data)
                            PreferenceManager.getInstance(this)
                                .putString(Keys.User, userInfo)
                            if (it.value.data?.fullName == null) {
                                loginActivity()

                            } else {
                                val activity = HomeActivity::class.java
                                startNewActivity(activity)
                                finish()
                            }

                        }

                        is Resource.Failure -> {
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
        } else {
            loginActivity()
        }
    }

    fun loginActivity() {
        val activity = AuthActivity::class.java
        startNewActivity(activity)
        finish()
    }
    override fun attachBaseContext(base: Context) {
        val languageUpdatedContext =
            LocaleHelper.setLocale(base, PreferenceManager.getInstance(base).getLanguage())
        val fontSafeContext = FontScaleContextWrapper.wrap(languageUpdatedContext)
        super.attachBaseContext(fontSafeContext)
    }
}