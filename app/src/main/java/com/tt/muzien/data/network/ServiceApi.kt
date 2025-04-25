package com.tt.muzien.data.network

import com.tt.muzien.data.responses.GenericResponse
import com.tt.muzien.data.responses.GetCategoriesResponse
import com.tt.muzien.data.responses.GetServicesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


/**
 * Created by Faheem Abbas on 15/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
interface ServiceApi {
    @Multipart
    @POST("v1/service")
    suspend fun addService(
        @Part image: MultipartBody.Part?=null,
        @Part("categoryId") categoryId: RequestBody,
        @Part("saloonId") saloonId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("duration") duration: RequestBody,
        @Part("price") price: RequestBody
    ): GenericResponse

    @GET("v1/service/mySaloonsServices")
    suspend fun getCategories(
    ): GetCategoriesResponse

    @GET("v1/service/mySaloonsServices")
    suspend fun getCategories(
        @Query("saloonIds") saloonIds: String,
    ): GetCategoriesResponse

    @GET("v1/service")
    suspend fun getServices(
    ): GetServicesResponse
}