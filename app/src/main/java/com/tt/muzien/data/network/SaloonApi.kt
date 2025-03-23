package com.tt.muzien.data.network

import com.tt.muzien.data.requests.AddHolidayRequest
import com.tt.muzien.data.requests.AddWorkingHourRequest
import com.tt.muzien.data.requests.UpdateSaloonRequest
import com.tt.muzien.data.requests.WorkHour
import com.tt.muzien.data.responses.AddHolidayResponse
import com.tt.muzien.data.responses.AddSaloonResponse
import com.tt.muzien.data.responses.AddWorkingHourResponse
import com.tt.muzien.data.responses.DeleteHolidayResponse
import com.tt.muzien.data.responses.GetSaloonDetailResponse
import com.tt.muzien.data.responses.GetSaloonResponse
import com.tt.muzien.data.responses.GetServiceProviderResponse
import com.tt.muzien.data.responses.GetSubscriptionsResponse
import com.tt.muzien.data.responses.SaloonDetailsData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * Created by Faheem Abbas on 16/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
interface SaloonApi {
    @GET("v1/saloon")
    suspend fun getSaloons(@Query("saloonId") saloonId: Int?, @Query("page") page	: Int?,
    ): GetSaloonResponse
    @GET("v1/saloon/{saloonId}")
    suspend fun getSaloonsDetail(@Path("saloonId") saloonId: Int?
    ): GetSaloonDetailResponse
    @Multipart
    @POST("v1/saloon")
    suspend fun addSaloon(
        @Part certificate: MultipartBody.Part?,
        @Part images: ArrayList<MultipartBody.Part>?,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("locationLat") locationLat: RequestBody,
        @Part("locationLong") locationLong: RequestBody,
        @Part("phoneNumber") phoneNumber: RequestBody,
        @Part("address") address: RequestBody,
        @PartMap hoursParts: MutableMap<String, RequestBody>,
        @PartMap holidays: MutableMap<String, RequestBody>
    ): AddSaloonResponse

    @PUT("v1/saloon/{saloonId}")
    suspend fun updateSaloon(
        @Path("saloonId") saloonId: Int, @Body requestData: UpdateSaloonRequest
    ): AddSaloonResponse

    @POST("v1/saloon/{saloonId}/holiday")
    suspend fun addHoliday(
        @Path("saloonId") saloonId: Int,
        @Body requestData: AddHolidayRequest
    ): AddHolidayResponse

    @POST("v1/saloon/{saloonId}/hours")
    suspend fun addHour(
        @Path("saloonId") saloonId: Int,
        @Body requestData: AddWorkingHourRequest
    ): AddWorkingHourResponse

    @DELETE("v1/saloon/{saloonId}/holiday/{holidayId}")
    suspend fun deleteHoliday(
        @Path("saloonId") saloonId: Int,
        @Path("holidayId") holidayId: Int
    ): DeleteHolidayResponse
    @DELETE("v1/saloon/{saloonId}/hours/{day}")
    suspend fun deleteWorkingHour(
        @Path("saloonId") saloonId: Int,
        @Path("day") day: String
    ): DeleteHolidayResponse

    @GET("v1/user/favourite/service-provider")
    suspend fun getServiceProviders(
    ): GetServiceProviderResponse

    @GET("v1/saloon/{saloonId}/subscription")
    suspend fun getSaloonsSubscriptions(
        @Path("saloonId") saloonId: Int,
    ): GetSubscriptionsResponse
}