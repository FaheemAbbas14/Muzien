package com.tt.muzien.data.repository

import com.tt.muzien.data.network.ServiceApi
import com.tt.muzien.data.requests.AddSaloonServiceRequest
import com.tt.muzien.data.requests.AddServiceSaloonRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody


/**
 * Created by Faheem Abbas on 15/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class ServiceRepository(
    private val api: ServiceApi,
) : BaseRepository() {

    suspend fun addService(
        image: MultipartBody.Part?,
        categoryId: RequestBody,
        saloonId: RequestBody,
        name: RequestBody,
        duration: RequestBody,
        price: RequestBody
    ) = safeApiCall {
        api.addService(image, categoryId, saloonId, name, duration, price)
    }

    suspend fun addSaloonService(
        request: AddServiceSaloonRequest
    ) = safeApiCall {
        api.addSaloonService(request)
    }

    suspend fun getCategories(
        saloonIds: String
    ) = safeApiCall {
        api.getCategories(saloonIds)

    }

    suspend fun getCategories() = safeApiCall {

        api.getCategories()

    }

    suspend fun getServices(
    ) = safeApiCall {
        api.getServices()
    }

    suspend fun getServicesStatus(
        serviceId: String
    ) = safeApiCall {
        api.getServicesStatus(serviceId)
    }

    suspend fun getServicesDetails(
        serviceId: String
    ) = safeApiCall {
        api.getServicesDetails(serviceId)
    }

    suspend fun updateService(
        serviceId: String,
        categoryId: String,
        name: String,
        duration: Int,
        price: Int
    ) = safeApiCall {
        api.updateService(serviceId, categoryId, name, duration, price)
    }
}