package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 18/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class GetMembersResponse( val status: Int,
                               val message: String,
                               val data: MembersData,)
data class MembersData(
    val members: List<Member>,
)

data class Member(
    val id: Long,
    val userId: Long,
    val saloonId: Long,
    val isAdmin: Boolean,
    val isActive: Boolean,
    val User: UserMember,
    val Saloon: SaloonMember,
)

data class UserMember(
    val id: Long,
    val fullName: String?,
    val picture: String?,
)

data class SaloonMember(
    val id: Long,
    val name: String,
)
