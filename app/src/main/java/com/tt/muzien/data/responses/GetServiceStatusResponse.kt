package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 08/05/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class GetServiceStatusResponse(
    val status: Int,
    val message: String,
    val data: List<ServiceSaloons>,
)

data class ServiceSaloons(
    val id: Long,
    val name: String,
    val address: String,
    val duration: Long,
    val price: Long,
    val serviceEnabled: Boolean,
)
