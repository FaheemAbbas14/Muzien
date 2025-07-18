package com.tt.muzien.data.responses

import com.tt.muzien.data.responses.UserServices


/**
 * Created by Faheem Abbas on 09/07/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class UserServicesResponse(
    val status: Int,
    val message: String,
    val data: List<UserServices>,
)

data class UserServices(
    val id: Long,
    val service: UserServiceDetails,
)

data class UserServiceDetails(
    val id: Long,
    val name: String,
)

