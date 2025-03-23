package com.tt.muzien.data.repository

import com.tt.muzien.data.network.ServiceApi
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
        image: MultipartBody.Part,
        categoryId: RequestBody,
        saloonId: RequestBody,
        name: RequestBody,
        duration: RequestBody,
        price: RequestBody
    ) = safeApiCall {
        api.addService(image, categoryId, saloonId, name, duration, price)
    }

    suspend fun getCategories(
    ) = safeApiCall {
        api.getCategories()
    }

    suspend fun getServices(
    ) = safeApiCall {
        api.getServices()
    }

}