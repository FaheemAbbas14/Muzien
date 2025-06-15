package com.tt.muzien.data.dto


/**
 * Created by Faheem Abbas on 24/01/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class SubscriptionData(
    var id: String,
    var name: String,
    var startDate: String,
    var endData: String,
    var isExpired:Boolean,
    var daysRemaining:Int,
)
