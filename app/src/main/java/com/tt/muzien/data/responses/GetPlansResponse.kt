package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 25/05/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class GetPlansResponse(
    val status: Int,
    val message: String,
    val data: List<Plans>,
)

data class Plans(
    val id: Long,
    val name: String,
    val durationInDays: Long,
    val actualFee: Long,
    val discountedFee: Long,
    val currency: String,
    val isActive: Boolean,
)

