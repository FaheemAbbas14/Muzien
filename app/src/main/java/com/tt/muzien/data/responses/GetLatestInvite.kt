package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 24/04/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class GetLatestInvite(
    val status: Int,
    val message: String,
    val data: InviteData,
)

data class InviteData(
    val id: Long,
    val invitedBy: Long,
    val userId: Long,
    val saloonId: Long,
    val InvitedBy: InvitedBy,
    val Saloon: Saloon,
)

data class InvitedBy(
    val id: Long,
    val fullName: String,
    val picture: String,
)

