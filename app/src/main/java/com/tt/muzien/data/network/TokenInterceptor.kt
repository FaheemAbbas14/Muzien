package com.zabihah.ui.data.network

import android.util.Log
import com.tt.muzien.data.requests.RefreshTokenRequest
import com.tt.muzien.data.responses.RefreshTokenResponse
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


/**
 * Created by Faheem Abbas on 08/08/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class TokenInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        Log.d("apiresponse", "code ${tokenManager.getAccessToken()}")
        val requestBuilder =
            if (tokenManager.getAccessToken() != null && tokenManager.getAccessToken() != "" && tokenManager.getRefreshToken() != "") {
                original.newBuilder()
                    .header("Authorization", "Bearer ${tokenManager.getAccessToken()}")

            } else {
                original.newBuilder()
            }

        var request = requestBuilder.method(original.method, original.body).build()
        val response = chain.proceed(request)

        Log.d("apiresponse", "code ${response.code}")
        // If the token is expired, refresh it
        if (response.code == 401) {
            synchronized(this) {
                Log.d("refreshToken", "old  ${tokenManager.getRefreshToken()}")
                if (tokenManager.getRefreshToken() != "") {
                    // Make sure the token is still expired
                    val refreshTokenResponse = refreshToken(tokenManager.getRefreshToken())
                    if (refreshTokenResponse != null && refreshTokenResponse.data!=null) {
                        tokenManager.saveAccessToken(refreshTokenResponse.data.token)
//                        tokenManager.saveRefreshToken(refreshTokenResponse.refreshToken)
//                        Log.d("refreshToken", "new  ${refreshTokenResponse.refreshToken}")
                        // Retry the request with the new token
                        request = request.newBuilder()
                            .header("Authorization", "Bearer ${refreshTokenResponse.data.token}")
                            .build()
                        return chain.proceed(request)
                    }
                }
            }
        }

        return response
    }

    private fun refreshToken(refreshToken: String?): RefreshTokenResponse? {
        return try {
            val response = tokenManager.getApiService().getRefreshToken(RefreshTokenRequest(
                refreshToken!!
            )).execute()
            if (response.body()?.status == 1) {
                response.body()
            } else {
                tokenManager.logout()
                null
            }
        } catch (e: IOException) {
            null
        }
    }

}
