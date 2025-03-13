package com.tt.muzien.data.network


import com.tt.muzien.data.responses.LoginResponse
import com.tt.muzien.data.responses.RefreshTokenResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {

  @GET("user")
  suspend fun getUser(): LoginResponse

  @POST("v1/auth/logout")
  suspend fun logout(): LoginResponse
  @POST("v1/User/refresh/jwt")
  fun getRefreshToken(@Query("refreshToken") refreshToken: String): Call<RefreshTokenResponse>
}