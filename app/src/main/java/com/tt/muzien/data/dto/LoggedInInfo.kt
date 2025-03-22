package com.tt.muzien.data.dto

import com.tt.muzien.data.responses.UserInfo


/**
 * Created by Faheem Abbas on 24/01/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
object LoggedInInfo {
    var userRole = "Admin"
    var userId: Long = 0
    var user: UserInfo? = null
    var fullName: String? = null
    var email: String? = null
    var phoneNumber: String? = null
    var nationality: String? = null
    var picture: String? = null
    var role: String? = null
}