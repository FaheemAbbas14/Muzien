package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 24/04/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class AcceptInviteResponse(val status: Int,
                                val message: String,
                                val data: AcceptInviteData,)
data class AcceptInviteData(
    val isAdmin: Boolean,
    val isActive: Boolean,
    val id: Long,
    val userId: Long,
    val saloonId: Long,
)
