package com.tt.muzien.data.network

import com.tt.muzien.data.responses.GetBookingResponse
import com.tt.muzien.data.responses.GetReviewsResponse
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by Faheem Abbas on 22/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
interface ReviewApi {
    @GET("v1/review")
    suspend fun getReviews(@Query("targetSaloon") targetSaloon: String?=null,@Query("reviewerId") reviewerId: String?=null): GetReviewsResponse
}