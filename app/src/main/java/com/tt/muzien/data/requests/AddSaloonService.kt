package com.tt.muzien.data.requests


/**
 * Created by Faheem Abbas on 08/05/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class AddServiceSaloonRequest(val saloonServices: ArrayList<AddSaloonService>)
data class AddSaloonService(
    val saloonId: String,
    val serviceId: String,
    val enabled: String,
    val duration: Int? = 0,
    val price: Int? = 0
)