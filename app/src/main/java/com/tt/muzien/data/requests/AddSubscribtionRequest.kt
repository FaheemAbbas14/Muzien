package com.tt.muzien.data.requests


/**
 * Created by Faheem Abbas on 28/03/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class AddSubscribtionRequest(
    val transactionId: String,
    val amount: Int,
    val startDate: String,
    val validTill: String,
)
