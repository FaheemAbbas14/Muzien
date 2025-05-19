package com.tt.muzien.data

import com.tt.muzien.data.responses.ReviewDetails


/**
 * Created by Faheem Abbas on 18/12/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class SaloonBookingData(
    var bookingId: String,
    var imageUrl: String,
    var name: String,
    var style: String,
    var personName: String,
    var service: String,
    var date: String,
    var time: String,
    var status: String,
    var duration: Long,
    var cancelledBy: String? = null,
    var cancelledReason: String? = null,
    val reviewDetails: ReviewDetails?,
)
