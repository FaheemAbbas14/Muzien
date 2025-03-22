package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 13/03/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class RefreshTokenResponse(
    val status: Int,
    val data: NewTokenData,
)

data class NewTokenData(
    val token: String,
)
