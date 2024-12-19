package com.tt.muzien.data.dto


/**
 * Created by Faheem Abbas on 05/08/2024.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class ReviewsInfo(
    val id: String,
    val icon: String,
    val name: String,
    val addedOn: String,
    val providedBy: String,
    val rating: String,
    val review: String,
    val images: List<String?>,
)
