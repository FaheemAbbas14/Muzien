package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 22/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class GetSubscriptionsResponse(
    val status: Int,
    val message: String,
    val data: SubscriptionData,
)

data class SubscriptionData(
    val items: List<Subscription>,
    val page: Long,
    val totalPages: Long,
    val total: Long,
    val limit: Long,
)

data class Subscription(
    val id: Long,
    val startDate: String,
    val validTill: String,
    val isExpired: Boolean,
    val Saloon: Saloon,
    val daysRemaining: Long,
)


