package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 12/03/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class VerifyLoginResponse(
    val status: Int,
    val data: Data,
    val message: String,
)

data class Data(
    val token: String,
    val refreshToken: String,
)