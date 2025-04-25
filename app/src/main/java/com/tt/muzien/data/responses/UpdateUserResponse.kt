package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 13/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class UpdateUserResponse(
    val status: Int,
    val message: String,
    val data: SignupData,
)

data class SignupData(
    val user: UserData,
)






