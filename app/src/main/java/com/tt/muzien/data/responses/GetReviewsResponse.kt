package com.tt.muzien.data.responses


/**
 * Created by Faheem Abbas on 22/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class GetReviewsResponse(
    val status: Int,
    val message: String,
    val data: ReviewData,
)

data class ReviewData(
    val reviews: List<Review>,
    val pagination: Pagination,
)

data class Review(
    val id: Long,
    val rating: Long,
    val comment: String,
    val createdAt: String,
    val saloon: Saloon,
    val user: ReviewUser,
    val reviewer: Reviewer,
    val images: List<Any?>,
)

data class Saloon(
    val id: Long,
    val name: String,
)

data class ReviewUser(
    val id: Long,
    val fullName: String?,
)

data class Reviewer(
    val id: Long,
    val fullName: String,
    val picture: String,
)


