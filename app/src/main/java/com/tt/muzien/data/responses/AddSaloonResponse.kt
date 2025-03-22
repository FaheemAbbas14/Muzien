package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 17/03/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class AddSaloonResponse(
    val status: Int,
    val message: String,
    val data: AddSaloon,
)
data class AddSaloon(
    val saloon: SaloonData,
)

