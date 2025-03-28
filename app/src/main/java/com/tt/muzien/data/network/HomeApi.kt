package com.tt.muzien.data.network

import com.tt.muzien.data.responses.GetAnalyticsResponse
import com.tt.muzien.data.responses.GetBookingResponse
import com.tt.muzien.data.responses.GetRevenueResponse
import com.tt.muzien.data.responses.LoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


/**
 * Created by Faheem Abbas on 26/11/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
interface HomeApi {
    @GET("v1/app/analytics")
    suspend fun getAnalytics(@Query("saloonIds") saloonIds: String?=null,@Query("startDate") startDate: String?=null,@Query("endDate") endDate: String?=null
    ):GetAnalyticsResponse
    @GET("v1/analytics/revenue/daily")
    suspend fun getWeeklyRevenue(@Query("saloonIds") saloonIds: String?=null,@Query("startDate") startDate: String?=null,@Query("endDate") endDate: String?=null
    ):GetRevenueResponse
    @GET("v1/analytics/revenue/monthly")
    suspend fun getMonthlyRevenue(@Query("saloonIds") saloonIds: String?=null,@Query("startDate") startDate: String?=null,@Query("endDate") endDate: String?=null
    ): GetRevenueResponse
}