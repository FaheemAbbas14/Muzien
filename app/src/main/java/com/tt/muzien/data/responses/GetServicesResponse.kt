package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 15/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class GetServicesResponse(
    val status: Int,
    val data: ServiceData,
)

data class ServiceData(
    val services: List<Service?>,
)
