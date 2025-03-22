package com.tt.muzien.data.repository

import com.tt.muzien.data.network.BookingApi


/**
 * Created by Faheem Abbas on 15/03/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class BookingRepository(
    private val api: BookingApi,
) : BaseRepository() {
    suspend fun getBookings(
        saloonIds: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        status: String? = null
    ) = safeApiCall {
        api.getBookings(saloonIds, startDate, endDate, status)
    }
}