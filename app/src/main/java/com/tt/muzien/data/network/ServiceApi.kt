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


/**
 * Created by Faheem Abbas on 15/03/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
interface ServiceApi {
    @Multipart
    @POST("v1/service")
    suspend fun addService(
        @Part image: MultipartBody.Part,
        @Part("categoryId") categoryId: RequestBody,
        @Part("saloonId") saloonId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("duration") duration: RequestBody,
        @Part("price") price: RequestBody
    ): GenericResponse

    @GET("v1/service/category")
    suspend fun getCategories(
    ): GetCategoriesResponse

    @GET("v1/service")
    suspend fun getServices(
    ): GetServicesResponse
}