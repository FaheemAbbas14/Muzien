package com.tt.muzien.data.network

import com.tt.muzien.data.responses.LoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Login API Request interface
 *
 */
interface AuthApi {

  @FormUrlEncoded
  @POST("login")
  suspend fun login(
    @Field("email") email: String,
    @Field("password") password: String
  ) : LoginResponse
}