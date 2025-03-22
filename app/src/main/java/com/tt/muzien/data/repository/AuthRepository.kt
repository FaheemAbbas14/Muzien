package com.tt.muzien.data.repository

import com.tt.muzien.constants.Keys
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.requests.LoginRequest
import com.tt.muzien.data.requests.RegisterRequest
import com.tt.muzien.data.requests.UpdateUser
import com.tt.muzien.data.requests.VerifyOTPRequest
import com.tt.muzien.utilities.PreferenceManager
import okhttp3.MultipartBody


class AuthRepository(
    private val api: AuthApi,
    private val preferences: PreferenceManager
) : BaseRepository() {

    suspend fun sendOTP(
        request: LoginRequest
    ) = safeApiCall {
        api.sendOTP(request)
    }

    suspend fun register(
        request: RegisterRequest
    ) = safeApiCall {
        api.register(request)
    }

    suspend fun verifyLogin(
        request: VerifyOTPRequest
    ) = safeApiCall {
        api.verifyLogin(request)
    }

    suspend fun my(
        request: Long
    ) = safeApiCall {
        api.my(request)
    }

    suspend fun updateUser(
        request: UpdateUser
    ) = safeApiCall {
        api.updateUser(request)
    }

    suspend fun uploadImage(
        request: MultipartBody.Part,
    ) = safeApiCall {
        api.uploadImage(request)
    }

    suspend fun saveAuthToken(token: String) {
        preferences.putString(Keys.Access_Token, token)
    }
}