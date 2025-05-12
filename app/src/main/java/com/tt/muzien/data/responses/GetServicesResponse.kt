package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 15/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class GetServicesResponse(
    val status: Int,
    val data: List<CategoryInfo>,
)

data class CategoryInfo(
    val id: Long,
    val name: String,
    val image: String,
    val services: List<ServiceInfo>,
)

data class ServiceInfo(
    val id: Long,
    val name: String,
    val image: String,
    val duration: Long,
    val price: Long,
    val saloonServices: List<SaloonServiceInfo>,
)

data class SaloonServiceInfo(
    val saloonId: Long,
    val duration: Long,
    val price: Long,
)
