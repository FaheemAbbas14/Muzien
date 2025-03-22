package com.tt.muzien.data.network

import com.tt.muzien.data.requests.AddMemberRequest
import com.tt.muzien.data.requests.AddWorkingHourRequest
import com.tt.muzien.data.responses.AddMemberResponse
import com.tt.muzien.data.responses.AddWorkingHourResponse
import com.tt.muzien.data.responses.GetMembersResponse
import com.tt.muzien.data.responses.GetSaloonResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * Created by Faheem Abbas on 15/03/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
interface MemberApi {
    @GET("v1/members/list")
    suspend fun getMembers(
    ): GetMembersResponse
    @GET("v1/members/list")
    suspend fun getMembers(@Query("saloonIds") page: String,
    ): GetMembersResponse
    @POST("v1/members/invite")
    suspend fun sendInvite(
        @Body requestData: AddMemberRequest
    ): AddMemberResponse
}