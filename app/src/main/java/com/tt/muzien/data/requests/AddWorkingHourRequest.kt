package com.tt.muzien.data.requests


/**
 * Created by Faheem Abbas on 17/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class AddWorkingHourRequest( val workHours: List<WorkHour>,)
data class WorkHour(
    val day: String,
    val openingTime: String,
    val closingTime: String,
)