package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 19/06/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class GetUserHolidaysResponse(
    val status: Int,
    val data: List<UserHoliday>,
    val message: String
)

data class UserHoliday(
    val id: Long,
    val userId: Long,
    val startDate: String,
    val endDate: String,
)

