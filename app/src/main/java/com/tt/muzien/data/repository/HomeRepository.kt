package com.tt.muzien.data.repository

import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.utilities.PreferenceManager


/**
 * Created by Faheem Abbas on 26/11/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class HomeRepository(
    private val api: HomeApi,
    private val preferences: PreferenceManager
) : BaseRepository() {

    suspend fun getAnalytics(
        saloonIds: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ) = safeApiCall {
        api.getAnalytics(saloonIds, startDate, endDate)
    }
    suspend fun getWeeklyRevenue(
        saloonIds: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ) = safeApiCall {
        api.getWeeklyRevenue(saloonIds, startDate, endDate)
    }
    suspend fun getMonthlyRevenue(
        saloonIds: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ) = safeApiCall {
        api.getMonthlyRevenue(saloonIds, startDate, endDate)
    }
}