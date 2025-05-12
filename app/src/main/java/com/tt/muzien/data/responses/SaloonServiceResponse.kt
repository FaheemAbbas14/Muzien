package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 06/05/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class SaloonServiceResponse(
    val status: Int,
    val message: String,
    val data: List<SaloonService>,
)

data class SaloonService(
    val id: Long,
    val saloonId: Long,
    val duration: Long,
    val price: Long,
    val service: ServiceDetails,
)

data class ServiceDetails(
    val id: Long,
    val name: String,
    val image: String,
    val category: CategoryDetails,
)

data class CategoryDetails(
    val id: Long,
    val name: String,
    val image: String,
)
