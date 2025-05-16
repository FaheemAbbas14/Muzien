package com.tt.muzien.data.repository

import com.tt.muzien.data.network.BookingApi
import com.tt.muzien.data.requests.UpdateBookingRequest


/**
 * Created by Faheem Abbas on 15/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class BookingRepository(
    private val api: BookingApi,
) : BaseRepository() {
    suspend fun getBookings(
        saloonIds: String? = null,
        serviceProviderId: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        status: String? = null,
        page: String? = null
    ) = safeApiCall {
        api.getBookings(saloonIds,serviceProviderId, startDate, endDate, status, page)
    }

    suspend fun getCalendarbar(
        saloonIds: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ) = safeApiCall {
        api.getCalendarbar(saloonIds, startDate, endDate)
    }

    suspend fun getAnalytics(
        saloonIds: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ) = safeApiCall {
        api.getAnalytics(saloonIds, startDate, endDate)
    }

    suspend fun updateBooking(
        bookingId: String,
        requestData: UpdateBookingRequest
    ) = safeApiCall {
        api.updateBooking(bookingId, requestData)
    }

}