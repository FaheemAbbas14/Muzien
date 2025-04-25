package com.tt.muzien.ui.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.data.repository.ServiceRepository
import com.tt.muzien.data.responses.GenericResponse
import com.tt.muzien.data.responses.GetCategoriesResponse
import com.tt.muzien.data.responses.GetSaloonResponse
import com.tt.muzien.data.responses.GetServicesResponse
import com.tt.muzien.ui.base.BaseViewModel
import com.tt.muzien.utilities.SingleEventLiveData
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody


/**
 * Created by Faheem Abbas on 15/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class ServiceViewModel(
    private val repository: ServiceRepository
) : BaseViewModel(repository) {
    private var saloonRepository: SaloonRepository? = null
    fun setSaloonRepo(saloonRepo: SaloonRepository) {
        saloonRepository = saloonRepo
    }

    private val _addService: MutableLiveData<Resource<GenericResponse>> = SingleEventLiveData()
    val addService: LiveData<Resource<GenericResponse>> get() = _addService

    private val _getCategories: MutableLiveData<Resource<GetCategoriesResponse>> =
        SingleEventLiveData()
    val getCategories: LiveData<Resource<GetCategoriesResponse>> get() = _getCategories

    private val _getServices: MutableLiveData<Resource<GetServicesResponse>> = SingleEventLiveData()
    val getServices: LiveData<Resource<GetServicesResponse>> get() = _getServices

    private val _getSaloon: MutableLiveData<Resource<GetSaloonResponse>> = SingleEventLiveData()
    val getSaloon: LiveData<Resource<GetSaloonResponse>> get() = _getSaloon

    fun getSaloons() = viewModelScope.launch {
        _getSaloon.value = saloonRepository?.getSaloons()
    }

    fun addService(
        image: MultipartBody.Part?,
        categoryId: RequestBody,
        saloonId: RequestBody,
        name: RequestBody,
        duration: RequestBody,
        price: RequestBody
    ) = viewModelScope.launch {
        _addService.value =
            repository.addService(image, categoryId, saloonId, name, duration, price)
    }

    fun getCategories(saloonIds: String? = null) = viewModelScope.launch {
        _getCategories.value = repository.getCategories(saloonIds)
    }

    fun getServices() = viewModelScope.launch {
        _getServices.value = repository.getServices()
    }
}