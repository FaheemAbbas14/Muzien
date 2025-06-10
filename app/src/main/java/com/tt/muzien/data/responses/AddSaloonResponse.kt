package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 17/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class AddSaloonResponse(
    val status: Int,
    val message: String,
    val data: AddSaloon,
)
data class AddSaloon(
    val numReviews: Long,
    val tRating: Long,
    val id: Long,
    val name: String,
    val description: String,
    val locationLat: String,
    val locationLong: String,
    val phoneNumber: String,
    val address: String,
    val ownerId: Long,
    val isActive: Boolean,
    val updatedAt: String,
    val createdAt: String,
    val certificate: String?,
    val subscriptionExpiry: String?,
)


