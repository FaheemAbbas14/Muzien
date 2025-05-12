package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 25/04/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class ServiceDetailsResponse(
    val status: Int,
    val message: String,
    val data: ServiceDetailsInfo
)

data class ServiceDetailsInfo(
    val id: Long,
    val addedBy: Long,
    val saloonId: Long,
    val categoryId: Long,
    val name: String,
    val duration: Long,
    val price: Long,
    val image: String,
    val category: Category,
)


