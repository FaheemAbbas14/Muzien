package com.tt.muzien.data.repository

import com.tt.muzien.data.network.UserApi
import com.tt.muzien.data.requests.LoginRequest
import com.tt.muzien.data.requests.UpdateUser
import com.tt.muzien.data.requests.VerifyOTPRequest
import okhttp3.MultipartBody


class UserRepository(
    private val api: UserApi,
) : BaseRepository() {

    suspend fun getUser() = safeApiCall {
        api.getUser()

    }

    suspend fun sendOTP(
        request: LoginRequest
    ) = safeApiCall {
        api.sendOTP(request)
    }

    suspend fun verifyLogin(
        request: VerifyOTPRequest
    ) = safeApiCall {
        api.verifyLogin(request)
    }

    suspend fun getUsers(
    ) = safeApiCall {
        api.getUsers()
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

    suspend fun logout(
    ) = safeApiCall {
        api.logout()
    }
    suspend fun deleteUser(
    ) = safeApiCall {
        api.deleteUser()
    }

}
