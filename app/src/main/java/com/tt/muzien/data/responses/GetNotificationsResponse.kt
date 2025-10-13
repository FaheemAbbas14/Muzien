package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 06/10/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class GetNotificationsResponse(
    val status: Int,
    val message: String,
    val data: NotificationData,
)

data class NotificationData(
    val items: List<NotificationItem>,
    val page: Long,
    val totalPages: Long,
    val total: Long,
    val limit: Long,
)

data class NotificationItem(
    val id: Long,
    val type: String,
    val notifType: Int,
    val itemId: Long?,
    val image: String,
    val title: String,
    val message: String,
)
