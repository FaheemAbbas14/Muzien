package com.zabihah.ui.data.network

import android.content.Context
import android.content.Intent
import com.tt.muzien.constants.Keys
import com.tt.muzien.data.network.RemoteDataSource
import com.tt.muzien.data.network.UserApi
import com.tt.muzien.ui.auth.AuthActivity
import com.tt.muzien.utilities.PreferenceManager


/**
 * Created by Faheem Abbas on 08/08/2024.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class TokenManager(val context: Context) {
    fun getAccessToken(): String? {
        return PreferenceManager.getInstance(context).getString(Keys.Access_Token, "")
    }

    fun getRefreshToken(): String? {
        return PreferenceManager.getInstance(context).getString(Keys.Refresh_Token, "")

    }

    fun getApiService(): UserApi {
        var remoteDataSource = RemoteDataSource()
        return remoteDataSource.buildApi(UserApi::class.java, context)
    }

    fun saveAccessToken(accessToken: String) {
        return PreferenceManager.getInstance(context).putString(Keys.Access_Token, accessToken)
    }

    fun saveRefreshToken(refreshToken: String) {
        return PreferenceManager.getInstance(context)
            .putString(Keys.Refresh_Token, refreshToken)

    }

    fun clearAccessToken() {
        return PreferenceManager.getInstance(context).putString(Keys.Access_Token, "")
    }

    fun clearRefreshToken() {
        return PreferenceManager.getInstance(context).putString(Keys.Refresh_Token, "")
    }

    fun logout() {
        clearAccessToken()
        clearRefreshToken()
        val intent = Intent(context, AuthActivity::class.java)
        context.startActivity(intent)
    }

}