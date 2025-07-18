package com.tt.muzien.data.dto

import com.tt.muzien.data.responses.ServiceInfo


/**
 * Created by Faheem Abbas on 19/12/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class ServiceInfo(
    val id: Int,
    val icon: String?=null,
    val name: String?=null,
    val duration: String?=null,
    val rate: String?=null,
    val service: ServiceInfo?=null
)
