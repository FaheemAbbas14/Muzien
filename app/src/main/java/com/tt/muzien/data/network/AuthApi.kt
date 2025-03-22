package com.tt.muzien.data.network

import com.tt.muzien.data.requests.LoginRequest
import com.tt.muzien.data.requests.RegisterRequest
import com.tt.muzien.data.requests.UpdateUser
import com.tt.muzien.data.requests.VerifyOTPRequest
import com.tt.muzien.data.responses.LoginResponse
import com.tt.muzien.data.responses.MyResponse
import com.tt.muzien.data.responses.RegisterResponse
import com.tt.muzien.data.responses.UpdateUserResponse
import com.tt.muzien.data.responses.VerifyLoginResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Login API Request interface
 *
 */
interface AuthApi {


    @POST("v1/auth/login")
    suspend fun sendOTP(
        @Body requestData: LoginRequest
    ): LoginResponse

    @POST("v1/auth/register")
    suspend fun register(
        @Body requestData: RegisterRequest
    ): RegisterResponse

    @POST("v1/auth/login-verify")
    suspend fun verifyLogin(
        @Body requestData: VerifyOTPRequest
    ): VerifyLoginResponse


    @GET("v1/user/{userId}")
    suspend fun my(
        @Path("userId") userId: Long
    ): MyResponse

    @POST("v1/auth/profile")
    suspend fun updateUser(
        @Body requestData: UpdateUser
    ): UpdateUserResponse

    @Multipart
    @POST("v1/auth/profile/image") // Change to your API endpoint
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
    ):MyResponse

}