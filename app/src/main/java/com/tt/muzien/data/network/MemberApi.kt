package com.tt.muzien.data.network

import com.tt.muzien.data.requests.AddHolidayRequest
import com.tt.muzien.data.requests.AddMemberRequest
import com.tt.muzien.data.requests.AddMemberService
import com.tt.muzien.data.requests.AddWorkingHourRequest
import com.tt.muzien.data.responses.AcceptInviteResponse
import com.tt.muzien.data.responses.AddMemberDataResponse
import com.tt.muzien.data.responses.AddMemberResponse
import com.tt.muzien.data.responses.GetLatestInvite
import com.tt.muzien.data.responses.GetMemberDetails
import com.tt.muzien.data.responses.GetMembersResponse
import com.tt.muzien.data.responses.GetServiceProviderResponse
import com.tt.muzien.data.responses.MarkManagerResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * Created by Faheem Abbas on 15/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
interface MemberApi {
    @GET("v1/members/mySaloonsMembers")
    suspend fun getMembers( @Query("isActive") isActive: Boolean? = null, @Query("status") status: Int? = null
    ): GetMembersResponse

    @GET("v1/members/{memberId}")
    suspend fun getMemberDetails(
        @Path("memberId") memberId: Int,
    ): GetMemberDetails

    @GET("v1/members/list")
    suspend fun getMembers(
        @Query("saloonIds") saloonIds: String, @Query("isActive") isActive: Boolean? = null,
    ): GetMembersResponse

    @POST("v1/members/invite")
    suspend fun sendInvite(
        @Body requestData: AddMemberRequest
    ): AddMemberResponse

    @POST("v1/members/{memberId}/inactivate")
    suspend fun inActiveMember(
        @Path("memberId") memberId: Int,
    ): MarkManagerResponse

    @DELETE("v1/members/{memberId}")
    suspend fun deleteMember(
        @Path("memberId") memberId: Int,
    ): MarkManagerResponse

    @POST("v1/members/{memberId}/make-manager")
    suspend fun makeManager(
        @Path("memberId") memberId: Int,
    ): MarkManagerResponse

    @GET("v1/members/invite/latest")
    suspend fun getLatestInvite(
    ): GetLatestInvite

    @POST("v1/members/invite/{inviteId}/accept-invite")
    suspend fun acceptInvite(
        @Path("inviteId") inviteId: Int,
    ): AcceptInviteResponse

    @POST("v1/user/service/{userId}/add")
    suspend fun addService(
        @Path("userId") userId: Int, @Body requestData: AddMemberService
    ): AddMemberDataResponse

    @POST("v1/user/{userId}/holiday")
    suspend fun addHoliday(
        @Path("userId") userId: Int, @Body requestData: AddHolidayRequest
    ): AddMemberDataResponse

    @POST("v1/user/{userId}/hours")
    suspend fun addWorkingHour(
        @Path("userId") userId: Int, @Body requestData: AddWorkingHourRequest
    ): AddMemberDataResponse

    @DELETE("v1/user/{userId}/hours/{day}")
    suspend fun removeWorkingHour(
        @Path("userId") userId: Int, @Path("day") day: String
    ): AddMemberDataResponse

    @DELETE("v1/user/{userId}/holiday/{holidayId}")
    suspend fun removeHoliday(
        @Path("userId") userId: Int, @Path("holidayId") holidayId: Int
    ): AddMemberDataResponse

    @GET("v1/members/my-saloom-members-summery")
    suspend fun getServiceProvider(
        @Query("isActive") isActive: Boolean? = null, @Query("isAdmin") isAdmin: Boolean? = null,
    ): GetServiceProviderResponse

    @POST("v1/members/{memberId}/remove-manager")
    suspend fun removeManager(
        @Path("memberId") memberId: Int,
    ): MarkManagerResponse

    @POST("v1/members/invite/{inviteId}/remove-invite")
    suspend fun removeInvite(
        @Path("inviteId") inviteId: Int,
    ): MarkManagerResponse
    @DELETE("v1/user/service/{userId}/{serviceId}")
    suspend fun deleteService(
        @Path("userId") userId: Int, @Path("serviceId") serviceId: Int
    ): AddMemberDataResponse
}