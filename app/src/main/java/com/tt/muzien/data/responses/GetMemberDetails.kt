package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 24/04/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class GetMemberDetails( val status: Int,
                        val message: String,
                        val data: MemberDetails,)
data class MemberDetails(
    val id: Long,
    val fullName: String?,
    val email: String?,
    val phoneNumber: String,
    val nationality: String,
    val picture: String?,
    val verified: Boolean,
    val workingToday: Boolean,
    val isMember: Boolean,
    val role: String,
    val credit: Long,
    val numReviews: Long,
    val tRating: Long,
    val createdAt: String,
    val updatedAt: String,
    val totalBookings: String,
    val saloon: Saloon?,
    val UserWorkHours: List<SaloonWorkHour?>,
    val UserHolidays: List<SaloonHoliday?>,
    val userServices: List<UserService?>,
)


data class UserService(
    val id: Long,
    val service: ServiceInfo,
)

