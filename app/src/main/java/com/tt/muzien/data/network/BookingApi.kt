package com.tt.muzien.data.network

import com.tt.muzien.data.requests.UpdateBookingRequest
import com.tt.muzien.data.responses.CalenderBookingResponse
import com.tt.muzien.data.responses.GetAnalyticsResponse
import com.tt.muzien.data.responses.GetBookingResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * Created by Faheem Abbas on 15/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
interface BookingApi {
    @GET("v1/booking")
    suspend fun getBookings(
        @Query("saloonIds") saloonIds: String? = null,
        @Query("serviceProviderIds") serviceProviderIds: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: String? = null
    ): GetBookingResponse

    @GET("v1/app/calendarbar")
    suspend fun getCalendarbar(
        @Query("saloonIds") saloonIds: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): CalenderBookingResponse

    @GET("v1/app/analytics")
    suspend fun getAnalytics(
        @Query("saloonIds") saloonIds: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): GetAnalyticsResponse

    @PUT("v1/booking/{bookingId}/status")
    suspend fun updateBooking(
        @Path("bookingId") bookingId: String, @Body requestData: UpdateBookingRequest,
    )
    @GET("v1/analytics/bookings/daily")
    suspend fun getDailyBookings(
        @Query("saloonIds") saloonIds: String? = null,
        @Query("serviceProviderIds") serviceProviderIds: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): GetBookingResponse
    @GET("v1/analytics/bookings/monthly")
    suspend fun getMonthlyBookings(
        @Query("saloonIds") saloonIds: String? = null,
        @Query("serviceProviderIds") serviceProviderIds: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): GetBookingResponse
}