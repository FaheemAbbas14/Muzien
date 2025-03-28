package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 27/03/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class GetAnalyticsResponse( val status: Int,
                            val message: String,
                            val data: AnalyticsData,)
data class AnalyticsData(
    val booking: List<BookingAnalytics>,
    val topPerformers: List<TopPerformer>,
    val totalEarning: String,
)

data class BookingAnalytics(
    val status: String,
    val total_count: String,
)

data class TopPerformer(
    val userId: Long,
    val fullName: String?,
    val picture: String,
    val totalBookings: String,
)