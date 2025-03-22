package com.tt.muzien.data.repository

import com.tt.muzien.data.network.SaloonApi
import com.tt.muzien.data.requests.AddHolidayRequest
import com.tt.muzien.data.requests.AddWorkingHourRequest
import com.tt.muzien.data.requests.UpdateSaloonRequest
import com.tt.muzien.data.requests.WorkHour
import okhttp3.MultipartBody
import okhttp3.RequestBody


/**
 * Created by Faheem Abbas on 16/03/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class SaloonRepository(
    private val api: SaloonApi,
) : BaseRepository() {
    suspend fun getSaloons(
    ) = safeApiCall {
        api.getSaloons()
    }
    suspend fun getServiceProviders(
    ) = safeApiCall {
        api.getServiceProviders()
    }
    suspend fun getSaloonsSubscriptions(saloonId: Int
    ) = safeApiCall {
        api.getSaloonsSubscriptions(saloonId)
    }

    suspend fun getSaloons(
        saloonId: Int
    ) = safeApiCall {
        api.getSaloons(saloonId)
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
        holidays: MutableMap<String, RequestBody>
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
        saloonId: Int, requestData: UpdateSaloonRequest
    ) = safeApiCall {
        api.updateSaloon(
            saloonId,
            requestData
        )
    }

    suspend fun addHour(
        saloonId: Int, request: AddWorkingHourRequest
    ) = safeApiCall {
        api.addHour(saloonId, request)
    }
    suspend fun deleteHoliday(
        saloonId: Int, holidayId: Int
    ) = safeApiCall {
        api.deleteHoliday(saloonId, holidayId)
    }
    suspend fun deleteWorkingHour(
        saloonId: Int, day: String
    ) = safeApiCall {
        api.deleteWorkingHour(saloonId, day)
    }

    suspend fun addHoliday(
        saloonId: Int, request: AddHolidayRequest
    ) = safeApiCall {
        api.addHoliday(saloonId, request)
    }
}