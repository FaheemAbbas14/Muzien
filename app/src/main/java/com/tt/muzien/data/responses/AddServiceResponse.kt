package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 06/05/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class AddServiceResponse(
    val status: Int,
    val code: Int,
    val data: AddServiceData,
    val message: String,
)

data class AddServiceData(
    val id: Long,
    val categoryId: Long,
    val name: String,
    val duration: Long,
    val price: Long,
    val addedBy: Long,
    val image: String?,
)
