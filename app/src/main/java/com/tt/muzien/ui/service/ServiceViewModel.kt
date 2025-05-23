package com.tt.muzien.ui.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.data.repository.ServiceRepository
import com.tt.muzien.data.requests.AddServiceSaloonRequest
import com.tt.muzien.data.responses.AddServiceResponse
import com.tt.muzien.data.responses.GetCategoriesResponse
import com.tt.muzien.data.responses.GetSaloonResponse
import com.tt.muzien.data.responses.GetServiceStatusResponse
import com.tt.muzien.data.responses.GetServicesResponse
import com.tt.muzien.data.responses.ServiceDetailsResponse
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

    private val _addService: MutableLiveData<Resource<AddServiceResponse>> = SingleEventLiveData()
    val addService: LiveData<Resource<AddServiceResponse>> get() = _addService

    private val _getCategories: MutableLiveData<Resource<GetCategoriesResponse>> =
        SingleEventLiveData()
    val getCategories: LiveData<Resource<GetCategoriesResponse>> get() = _getCategories
    private val _getSaloonCategories: MutableLiveData<Resource<GetServicesResponse>> =
        SingleEventLiveData()
    val getSaloonCategories: LiveData<Resource<GetServicesResponse>> get() = _getSaloonCategories

    private val _getServices: MutableLiveData<Resource<GetServicesResponse>> = SingleEventLiveData()
    val getServices: LiveData<Resource<GetServicesResponse>> get() = _getServices

    private val _getServicesStatus: MutableLiveData<Resource<GetServiceStatusResponse>> =
        SingleEventLiveData()
    val getServicesStatus: LiveData<Resource<GetServiceStatusResponse>> get() = _getServicesStatus


    private val _getServicesDetails: MutableLiveData<Resource<ServiceDetailsResponse>> =
        SingleEventLiveData()
    val getServicesDetails: LiveData<Resource<ServiceDetailsResponse>> get() = _getServicesDetails


    private val _getSaloon: MutableLiveData<Resource<GetSaloonResponse>> = SingleEventLiveData()
    val getSaloon: LiveData<Resource<GetSaloonResponse>> get() = _getSaloon
    private val _addSaloonService: MutableLiveData<Resource<AddServiceResponse>> =
        SingleEventLiveData()
    val addSaloonService: LiveData<Resource<AddServiceResponse>> get() = _addSaloonService


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

    fun updateService(
        serviceId: String,
        categoryId: String,
        name: String,
        duration: Int,
        price: Int
    ) = viewModelScope.launch {
        _addService.value =
            repository.updateService(serviceId, categoryId, name, duration, price)
    }


    fun getCategories() = viewModelScope.launch {
        _getCategories.value = repository.getCategories()
    }

    fun getAllCategories() = viewModelScope.launch {
        _getCategories.value = repository.getAllCategories()
    }

    fun getCategories(saloonIds: String) = viewModelScope.launch {
        _getSaloonCategories.value = repository.getCategories(saloonIds)
    }

    fun getServices() = viewModelScope.launch {
        _getServices.value = repository.getServices()
    }

    fun getServicesStatus(serviceId: String) = viewModelScope.launch {
        _getServicesStatus.value = repository.getServicesStatus(serviceId)
    }

    fun getServicesDetails(serviceId: String) = viewModelScope.launch {
        _getServicesDetails.value = repository.getServicesDetails(serviceId)
    }

    fun addSaloonService(request: AddServiceSaloonRequest) = viewModelScope.launch {
        _addSaloonService.value = repository.addSaloonService(request)
    }
}