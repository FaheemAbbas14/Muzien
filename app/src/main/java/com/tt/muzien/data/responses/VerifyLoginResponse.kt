package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 12/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class VerifyLoginResponse(
    val status: Int,
    val data: TokenData,
    val message: String,
)

data class TokenData(
    val token: String,
    val refreshToken: String,
)