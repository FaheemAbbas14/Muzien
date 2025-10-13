package com.tt.muzien.data.network


import com.tt.muzien.data.requests.LoginRequest
import com.tt.muzien.data.requests.RefreshTokenRequest
import com.tt.muzien.data.requests.UpdateUser
import com.tt.muzien.data.requests.VerifyOTPRequest
import com.tt.muzien.data.responses.GetNotificationsResponse
import com.tt.muzien.data.responses.GetsUsersResponse
import com.tt.muzien.data.responses.LoginResponse
import com.tt.muzien.data.responses.MyResponse
import com.tt.muzien.data.responses.RefreshTokenResponse
import com.tt.muzien.data.responses.UpdateUserResponse
import com.tt.muzien.data.responses.VerifyLoginResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface UserApi {

    @GET("user")
    suspend fun getUser(): LoginResponse

    @POST("v1/auth/logout")
    suspend fun logout(): LoginResponse

    @POST("v1/auth/refresh-token")
    fun getRefreshToken(@Body requestData: RefreshTokenRequest): Call<RefreshTokenResponse>

    @GET("v1/user")
    suspend fun getUsers(
    ): GetsUsersResponse

    @GET("v1/user/{userId}")
    suspend fun my(
        @Path("userId") userId: Long,
    ): MyResponse

    @POST("v1/auth/profile")
    suspend fun updateUser(
        @Body requestData: UpdateUser,
    ): UpdateUserResponse

    @Multipart
    @POST("v1/auth/profile/image") // Change to your API endpoint
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
    ): MyResponse

    @POST("v1/auth/update-verify")
    suspend fun verifyLogin(
        @Body requestData: VerifyOTPRequest,
    ): VerifyLoginResponse

    @POST("v1/auth/update")
    suspend fun sendOTP(
        @Body requestData: LoginRequest,
    ): LoginResponse

    @DELETE("v1/auth/profile")
    suspend fun deleteUser(
    )

    @GET("v1/notification")
    suspend fun getNotifications(): GetNotificationsResponse

    @GET("v1/notification/{type}")
    suspend fun getNotificationsByType(
        @Path("type") type: String,
    ): GetNotificationsResponse
}