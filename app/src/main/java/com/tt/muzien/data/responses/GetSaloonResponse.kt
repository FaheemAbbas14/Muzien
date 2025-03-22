package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 16/03/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class GetSaloonResponse(
    val status: Int,
    val message: String,
    val data: SaloonData,
)

data class SaloonData(
    val saloons: List<SaloonInfo>,
    val pagination: Pagination,
)

data class SaloonInfo(
    val id: Long,
    val ownerId: Long,
    val name: String,
    val description: String? = null,
    val phoneNumber: String,
    val address: String?,
    val locationLat: String,
    val locationLong: String,
    val isActive: Boolean,
    val numReviews: Long,
    val tRating: Long,
    val certificate: String,
    val SaloonImages: List<SaloonImage>,
    val SaloonWorkHours: List<SaloonWorkHour>,
    val SaloonHolidays: List<SaloonHoliday>,
)

data class SaloonImage(
    val image: String,
)

data class SaloonWorkHour(
    val day: String,
    val openingTime: String,
    val closingTime: String,
)

data class SaloonHoliday(
    val id: Long,
    val startDate: String,
    val endDate: String,
)

data class Pagination(
    val total: Long,
    val page: Long,
    val limit: Long,
    val totalPages: Long,
)

