package com.tt.muzien.data.network


import com.tt.muzien.data.responses.LoginResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {

  @GET("user")
  suspend fun getUser(): LoginResponse

  @POST("logout")
  suspend fun logout(): ResponseBody
}