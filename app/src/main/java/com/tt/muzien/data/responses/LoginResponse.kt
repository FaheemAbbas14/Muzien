package com.tt.muzien.data.responses

data class LoginResponse(
  val status: Int,
  val message: String,
  val data: Data?,
)

data class Data(
  val otp: Otp,
)

data class Otp(
  val userId: Long,
  val phoneNumber: String,
  val otp: String,
  val expiryDate: String,
  val sent: Boolean,
)