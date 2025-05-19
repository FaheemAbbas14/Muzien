package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 19/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class GetBookingResponse(
    val status: Int,
    val message: String,
    val data: BookingData,
)

data class BookingData(
    val items: List<Booking>,
    val page: Long,
    val totalPages: Long,
    val total: Long,
    val limit: Long,
)

data class Booking(
    val id: Long,
    val date: String,
    val time: String,
    val duration: Long,
    val cost: Long,
    val status: String,
    val bookingServices: List<BookingService>,
    val customer: Customer,
    val serviceProvider: ServiceProvider,
    val saloon: SaloonDetails,
    val bookingRemarks: List<BookingRemark?>,
    val bookingReviews: List<BookingReview?>,
)

data class BookingService(
    val id: Long,
    val serviceDetails: ServiceDetails,
)

data class BookingReview(
    val id: Long,
    val reviewDetails: ReviewDetails,
)

data class ReviewDetails(
    val id: Long,
    val rating: Long,
    val comment: String,
    val createdAt: String,
    val updatedAt: String,
    val reviewer: BookingReviewer,
    val images: List<Image>,
)

data class BookingReviewer(
    val id: Long,
    val fullName: String,
    val picture: Any?,
    val role: String,
)

data class Image(
    val image: String,
)

data class Customer(
    val id: Long,
    val fullName: String?,
    val picture: String?,
)

data class ServiceProvider(
    val id: Long,
    val fullName: String?,
    val picture: String?,
)

data class SaloonDetails(
    val id: Long,
    val name: String,
)
data class BookingRemark(
    val id: Long,
    val movedFrom: String,
    val movedTo: String,
    val comment: String,
    val user: RemarksUser,
)

data class RemarksUser(
    val id: Long,
    val fullName: String,
)


