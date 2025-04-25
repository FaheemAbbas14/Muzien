package com.tt.muzien.data.responses

import com.fasterxml.jackson.annotation.JsonProperty


/**
 * Created by Faheem Abbas on 19/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class GetSaloonDetailResponse(
    val status: Int,
    val message: String,
    val data: SaloonDetailsInfo,
)
data class SaloonDetailsInfo(
    val id: Long,
    val ownerId: Long,
    val name: String,
    val description: String,
    val phoneNumber: String,
    val address: String,
    val locationLat: String,
    val locationLong: String,
    val certificate: String,
    val subscriptionExpiry: String,
    val isActive: Boolean,
    val numReviews: Long,
    val tRating: Long,
    val createdAt: String,
    val updatedAt: String,
    @JsonProperty("SaloonImages")
    val saloonImages: List<SaloonImage?>,
    @JsonProperty("SaloonWorkHours")
    val SaloonWorkHours: List<SaloonWorkHour>,
    @JsonProperty("SaloonHolidays")
    val SaloonHolidays: List<SaloonHoliday>,
)


