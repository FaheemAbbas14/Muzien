package com.tt.muzien.ui.bookings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.BookingRepository
import com.tt.muzien.data.repository.MemberRepository
import com.tt.muzien.data.requests.UpdateBookingRequest
import com.tt.muzien.data.responses.AcceptInviteResponse
import com.tt.muzien.data.responses.CalenderBookingResponse
import com.tt.muzien.data.responses.GetAnalyticsResponse
import com.tt.muzien.data.responses.GetBookingResponse
import com.tt.muzien.data.responses.GetLatestInvite
import com.tt.muzien.ui.base.BaseViewModel
import com.tt.muzien.utilities.SingleEventLiveData
import kotlinx.coroutines.launch


/**
 * Created by Faheem Abbas on 15/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class BookingViewModel(
    private val repository: BookingRepository
) : BaseViewModel(repository) {
    private var memberRepository: MemberRepository? = null
    fun setMemberRepo(memberRepo: MemberRepository) {
        memberRepository = memberRepo
    }

    private val _getBooking: MutableLiveData<Resource<GetBookingResponse>> = SingleEventLiveData()
    val getBooking: LiveData<Resource<GetBookingResponse>> get() = _getBooking
    private val _getCalendarbar: MutableLiveData<Resource<CalenderBookingResponse>> =
        SingleEventLiveData()
    val getCalendarbar: LiveData<Resource<CalenderBookingResponse>> get() = _getCalendarbar
    private val _getAnalytics: MutableLiveData<Resource<GetAnalyticsResponse>> =
        SingleEventLiveData()
    val getAnalytics: LiveData<Resource<GetAnalyticsResponse>> get() = _getAnalytics
    private val _getLatestInvite: MutableLiveData<Resource<GetLatestInvite>> = SingleEventLiveData()
    val getLatestInvite: LiveData<Resource<GetLatestInvite>> get() = _getLatestInvite
    private val _updateBooking: MutableLiveData<Resource<Unit>> = SingleEventLiveData()
    val updateBooking: LiveData<Resource<Unit>> get() = _updateBooking
    private val _acceptInvite: MutableLiveData<Resource<AcceptInviteResponse>> =
        SingleEventLiveData()
    val acceptInvite: LiveData<Resource<AcceptInviteResponse>> get() = _acceptInvite

    fun getBookings(
        saloonIds: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        status: String? = null,
        page: String? = null
    ) = viewModelScope.launch {
        _getBooking.value = repository.getBookings(saloonIds, startDate, endDate, status, page)
    }

    fun getCalendarbar(
        saloonIds: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ) = viewModelScope.launch {
        _getCalendarbar.value = repository.getCalendarbar(saloonIds, startDate, endDate)
    }

    fun getAnalytics(
        saloonIds: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ) = viewModelScope.launch {
        _getAnalytics.value = repository.getAnalytics(saloonIds, startDate, endDate)
    }

    fun getLatestInvite(
    ) = viewModelScope.launch {
        _getLatestInvite.value = memberRepository?.getLatestInvite()
    }

    fun acceptInvite(
        inviteId: Int
    ) = viewModelScope.launch {
        _acceptInvite.value = memberRepository?.acceptInvite(inviteId)
    }

    fun updateBooking(
        bookingId: String,
        requestData: UpdateBookingRequest
    ) = viewModelScope.launch {
        _updateBooking.value = repository.updateBooking(bookingId, requestData)
    }
}