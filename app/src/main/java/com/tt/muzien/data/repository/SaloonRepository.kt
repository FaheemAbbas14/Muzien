package com.tt.muzien.data.repository

import com.tt.muzien.data.network.SaloonApi
import com.tt.muzien.data.requests.AddHolidayRequest
import com.tt.muzien.data.requests.AddSubscriptionRequest
import com.tt.muzien.data.requests.AddWorkingHourRequest
import com.tt.muzien.data.requests.UpdateSaloonRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody


/**
 * Created by Faheem Abbas on 16/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class SaloonRepository(
    private val api: SaloonApi,
) : BaseRepository() {
    suspend fun getSaloons(
        page: Int? = null,
        isActive: Boolean? = null,
        status: String? = null,
    ) = safeApiCall {
        api.getSaloons(page,isActive,status)
    }

    suspend fun getSaloonsDetail(
        saloonId: Int? = null,
    ) = safeApiCall {
        api.getSaloonsDetail(saloonId)
    }

    suspend fun getServiceProviders(
    ) = safeApiCall {
        api.getServiceProviders()
    }

    suspend fun addSubscriptions(
        saloonId: Int, requestData: AddSubscriptionRequest,
    ) = safeApiCall {
        api.addSubscriptions(saloonId, requestData)
    }

    suspend fun updateSubscriptions(
        saloonId: Int, subscriptionId: Int, requestData: AddSubscriptionRequest,
    ) = safeApiCall {
        api.updateSubscriptions(saloonId, subscriptionId, requestData)
    }

    suspend fun getPlans(
        name: String? = null,
        isActive: Boolean? = null,
        days: Int? = null,
    ) = safeApiCall {

        api.getPlans(name, isActive, days)

    }

    suspend fun getSaloonsSubscriptions(
        saloonId: Int?,
    ) = safeApiCall {
        if (saloonId != null) {
            api.getSpecificSaloonsSubscriptions(saloonId)
        } else {
            api.getSaloonsSubscriptions()
        }
    }

    suspend fun addSaloon(
        certificate: MultipartBody.Part?,
        images: ArrayList<MultipartBody.Part>?,
        name: RequestBody,
        description: RequestBody,
        locationLat: RequestBody,
        locationLong: RequestBody,
        phoneNumber: RequestBody,
        address: RequestBody,
        hoursParts: MutableMap<String, RequestBody>,
        holidays: MutableMap<String, RequestBody>,
    ) = safeApiCall {
        api.addSaloon(
            certificate,
            images,
            name,
            description,
            locationLat,
            locationLong,
            phoneNumber,
            address,
            hoursParts,
            holidays
        )
    }

    suspend fun updateSaloon(
        saloonId: Int, requestData: UpdateSaloonRequest,
    ) = safeApiCall {
        api.updateSaloon(
            saloonId,
            requestData
        )
    }

    suspend fun uploadSaloonCertificate(
        saloonId: Int, requestData: MultipartBody.Part?,
    ) = safeApiCall {
        api.uploadSaloonCertificate(
            saloonId,
            requestData
        )
    }

    suspend fun addHour(
        saloonId: Int, request: AddWorkingHourRequest,
    ) = safeApiCall {
        api.addHour(saloonId, request)
    }

    suspend fun deleteHoliday(
        saloonId: Int, holidayId: Int,
    ) = safeApiCall {
        api.deleteHoliday(saloonId, holidayId)
    }

    suspend fun deleteWorkingHour(
        saloonId: Int, day: String,
    ) = safeApiCall {
        api.deleteWorkingHour(saloonId, day)
    }

    suspend fun addHoliday(
        saloonId: Int, request: AddHolidayRequest,
    ) = safeApiCall {
        api.addHoliday(saloonId, request)
    }
}