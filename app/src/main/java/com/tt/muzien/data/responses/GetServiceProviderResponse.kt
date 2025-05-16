package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 22/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class GetServiceProviderResponse( val status: Int,
                                       val message: String,
                                       val data: List<ServiceProviderInfo>,)
data class ServiceProviderInfo(
    val id: Long,
    val isAdmin: Boolean,
    val isActive: Boolean,
    val User: UserInfo,
)

data class UserInfo(
    val id: Long,
    val fullName: String,
)
