package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 17/06/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class GetBookingDetailsResponse(
    val status: Int,
    val data: BookingDetailsData,
    var message: String,
)

data class BookingDetailsData(
    val id: Long,
    val addedBy: Long,
    val saloonId: Long,
    val serviceProviderId: Long,
    val date: String,
    val time: String,
    val duration: Long,
    val cost: Long,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val bookingServices: List<BookingDetailsService>,
    val customer: BookingDetailsCustomer,
    val serviceProvider: BookingDetailsServiceProvider,
    val saloon: BookingDetailsSaloon,
    val bookingRemarks: List<BookingDetailsRemark>,
    val bookingReviews: List<Any?>,
)

data class BookingDetailsService(
    val id: Long,
    val serviceDetails: BookingDetailsServiceDetails,
)

data class BookingDetailsServiceDetails(
    val id: Long,
    val name: String,
    val duration: Long,
    val price: Long,
)

data class BookingDetailsCustomer(
    val id: Long,
    val fullName: String,
    val phoneNumber: String,
    val picture: String?,
)

data class BookingDetailsServiceProvider(
    val id: Long,
    val fullName: String,
    val nationality: String,
    val picture: String,
)

data class BookingDetailsSaloon(
    val id: Long,
    val name: String,
    val address: String,
    val locationLat: String,
    val locationLong: String,
    val ownerId: Long,
)

data class BookingDetailsRemark(
    val id: Long,
    val movedFrom: String,
    val movedTo: String,
    val comment: String,
    val createdAt: String,
    val user: BookingDetailsUser,
)

data class BookingDetailsUser(
    val id: Long,
    val fullName: String,
)

