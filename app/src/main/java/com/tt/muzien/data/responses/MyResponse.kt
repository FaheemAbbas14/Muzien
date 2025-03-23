package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 13/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class MyResponse(
    val status: Int,
    val data: UserData?,
    val message: String,
)

data class UserData(
    val user: UserInfo,
)

data class UserInfo(
    val id: Long,
    val fullName: String?,
    val email: String?,
    val phoneNumber: String,
    val nationality: String,
    var picture: String,
    val verified: Boolean,
    val role: String,
    val credit: Long,
    val numReviews: Long,
    val tRating: Long,
    val createdAt: String,
    val updatedAt: String,
)

