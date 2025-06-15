package com.tt.muzien.data.dto


/**
 * Created by Faheem Abbas on 03/01/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class ServiceSaloon(
    val saloonId: String,
    val serviceId: String,
    val name: String,
    val address: String?="",
    var duration: String,
    val currency: String,
    var price: String,
    var isEnabled: Boolean
)
