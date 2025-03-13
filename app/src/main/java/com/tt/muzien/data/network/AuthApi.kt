package com.tt.muzien.data.network

import com.tt.muzien.data.requests.LoginRequest
import com.tt.muzien.data.requests.RegisterRequest
import com.tt.muzien.data.requests.VerifyOTPRequest
import com.tt.muzien.data.responses.LoginResponse
import com.tt.muzien.data.responses.RegisterResponse
import com.tt.muzien.data.responses.VerifyLoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

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
}