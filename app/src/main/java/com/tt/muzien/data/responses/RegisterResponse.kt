package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 12/03/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class RegisterResponse(
    val status: Int,
    val message: String,
    val otp: Otp,
)

data class Otp(
    val userId: Long,
    val phoneNumber: String,
    val otp: String,
    val expiryDate: String,
    val sent: Boolean,
)
