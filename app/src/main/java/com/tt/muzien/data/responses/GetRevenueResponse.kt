package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 27/03/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class GetRevenueResponse(
    val status: Int,
    val message: String,
    val data: RevenueData?
)

data class RevenueData(
    val revenue: List<Revenue>,
)

data class Revenue(
    val id: Long,
    val date: String?,
    val count: Long,
    val updatedAt: String,
    val month: Long?,
    val year: Long?,
)