package com.tt.muzien.data.requests


/**
 * Created by Faheem Abbas on 17/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class AddHolidayRequest(
    val holidays: List<Holiday>,
)

data class Holiday(
    val startDate: String,
    val endDate: String,
)