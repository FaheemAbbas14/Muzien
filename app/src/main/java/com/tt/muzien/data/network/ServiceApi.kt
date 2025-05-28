package com.tt.muzien.data.network

import com.tt.muzien.data.requests.AddServiceSaloonRequest
import com.tt.muzien.data.requests.UpdateServiceRequest
import com.tt.muzien.data.responses.AddServiceResponse
import com.tt.muzien.data.responses.GetCategoriesResponse
import com.tt.muzien.data.responses.GetServiceStatusResponse
import com.tt.muzien.data.responses.GetServicesResponse
import com.tt.muzien.data.responses.ServiceDetailsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
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
        @Part image: MultipartBody.Part? = null,
        @Part("categoryId") categoryId: RequestBody,
        @Part("saloonId") saloonId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("duration") duration: RequestBody,
        @Part("price") price: RequestBody,
    ): AddServiceResponse

    @GET("v1/service/myservices")
    suspend fun getCategories(
    ): GetCategoriesResponse

    @GET("v1/service/category")
    suspend fun getAllCategories(
    ): GetCategoriesResponse

    @GET("v1/saloon/service/{saloonId}")
    suspend fun getCategories(
        @Path("saloonId") saloonId: String,
    ): GetServicesResponse

    @GET("v1/service/mySaloonsServices")
    suspend fun getServices(
    ): GetServicesResponse

    @GET("v1/saloon/service/status")
    suspend fun getServicesStatus(
        @Query("serviceId") serviceId: String,
    ): GetServiceStatusResponse


    @PUT("v1/service/{serviceId}")
    suspend fun updateService(
        @Path("serviceId") serviceId: String,
        @Part("categoryId") categoryId: String,
        @Part("name") name: String,
        @Part("duration") duration: Int,
        @Part("price") price: Int,
    ): AddServiceResponse

    @GET("v1/service/{serviceId}")
    suspend fun getServicesDetails(
        @Path("serviceId") serviceId: String,
    ): ServiceDetailsResponse


    @POST("v1/saloon/service")
    suspend fun addSaloonService(
        @Body requestData: AddServiceSaloonRequest,
    ): AddServiceResponse

    @PUT("v1/service/{serviceId}")
    suspend fun updateService(
        @Path("serviceId") serviceId: Int, @Body requestData: UpdateServiceRequest,
    )
}