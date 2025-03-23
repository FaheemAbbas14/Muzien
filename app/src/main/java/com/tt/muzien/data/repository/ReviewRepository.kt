package com.tt.muzien.data.repository

import com.tt.muzien.data.network.ReviewApi


/**
 * Created by Faheem Abbas on 22/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class ReviewRepository(
    private val api: ReviewApi,
) : BaseRepository() {
    suspend fun getReviews(
        saloonId: String? = null, reviewId: String? = null
    ) = safeApiCall {
        api.getReviews(saloonId, reviewId)
    }
}