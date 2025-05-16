package com.tt.muzien.data.repository

import com.tt.muzien.data.network.MemberApi
import com.tt.muzien.data.requests.AddHolidayRequest
import com.tt.muzien.data.requests.AddMemberRequest
import com.tt.muzien.data.requests.AddMemberService
import com.tt.muzien.data.requests.AddWorkingHourRequest


/**
 * Created by Faheem Abbas on 15/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class MemberRepository(
    private val api: MemberApi,
) : BaseRepository() {

    suspend fun getMembers(  isActive: Boolean?=null
    ) = safeApiCall {
        api.getMembers(isActive)
    }

    suspend fun getMemberDetails(
        memberId: Int
    ) = safeApiCall {
        api.getMemberDetails(memberId)
    }

    suspend fun getMembers(
        saloonId: String,
        isActive: Boolean?=null
    ) = safeApiCall {
        api.getMembers(saloonId,isActive)
    }

    suspend fun sendInvite(
        request: AddMemberRequest
    ) = safeApiCall {
        api.sendInvite(request)
    }

    suspend fun inActiveMember(memberId: Int) = safeApiCall {
        api.inActiveMember(memberId)
    }

    suspend fun deleteMember(memberId: Int) = safeApiCall {
        api.deleteMember(memberId)
    }

    suspend fun makeManager(memberId: Int) = safeApiCall {
        api.makeManager(memberId)
    }

    suspend fun getLatestInvite() = safeApiCall {
        api.getLatestInvite()
    }

    suspend fun acceptInvite(inviteId: Int) = safeApiCall {
        api.acceptInvite(inviteId)
    }

    suspend fun addService(userId: Int, request: AddMemberService) = safeApiCall {
        api.addService(userId, request)
    }

    suspend fun addHoliday(userId: Int, request: AddHolidayRequest) = safeApiCall {
        api.addHoliday(userId, request)
    }

    suspend fun addWorkingHour(userId: Int, request: AddWorkingHourRequest) = safeApiCall {
        api.addWorkingHour(userId, request)
    }

    suspend fun removeWorkingHour(userId: Int, day: String) = safeApiCall {
        api.removeWorkingHour(userId, day)
    }

    suspend fun removeHoliday(userId: Int, holidayId: Int) = safeApiCall {
        api.removeHoliday(userId, holidayId)
    }
    suspend fun getServiceProvider(isActive: Boolean, isAdmin: Boolean?) = safeApiCall {
        api.getServiceProvider(isActive, isAdmin)
    }
}