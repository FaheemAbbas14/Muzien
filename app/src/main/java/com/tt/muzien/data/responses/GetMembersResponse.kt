package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 18/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class GetMembersResponse( val status: Int,
                               val message: String,
                               val data: List<MembersData>,)
data class MembersData(
    val id: Long,
    val userId: Long,
    val saloonId: Long,
    val isMember: Boolean,
    val isAdmin: Boolean,
    val isActive: Boolean,
    val todayBookings: String,
    val numReviews: Long,
    val tRating: Long,
    val User: UserMember,
    val Saloon: SaloonMember,
)

data class UserMember(
    val id: Long,
    val fullName: String?,
    val picture: String,
    val role: String,
)

data class SaloonMember(
    val id: Long,
    val name: String,
)
