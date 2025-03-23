package com.tt.muzien.data.repository

import com.tt.muzien.data.network.MemberApi
import com.tt.muzien.data.requests.AddMemberRequest


/**
 * Created by Faheem Abbas on 15/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class MemberRepository(
    private val api: MemberApi,
) : BaseRepository() {

    suspend fun getMembers(
    ) = safeApiCall {
        api.getMembers()
    }
    suspend fun getMembers(saloonId: String
    ) = safeApiCall {
        api.getMembers(saloonId)
    }
    suspend fun sendInvite(request: AddMemberRequest
    ) = safeApiCall {
        api.sendInvite(request)
    }
}