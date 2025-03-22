package com.tt.muzien.ui.saloon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.data.requests.AddHolidayRequest
import com.tt.muzien.data.requests.AddWorkingHourRequest
import com.tt.muzien.data.requests.UpdateSaloonRequest
import com.tt.muzien.data.responses.AddHolidayResponse
import com.tt.muzien.data.responses.AddSaloonResponse
import com.tt.muzien.data.responses.AddWorkingHourResponse
import com.tt.muzien.data.responses.DeleteHolidayResponse
import com.tt.muzien.data.responses.GetSaloonDetailResponse
import com.tt.muzien.data.responses.GetSaloonResponse
import com.tt.muzien.data.responses.GetServiceProviderResponse
import com.tt.muzien.data.responses.GetSubscriptionsResponse
import com.tt.muzien.ui.base.BaseViewModel
import com.tt.muzien.utilities.SingleEventLiveData
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody


/**
 * Created by Faheem Abbas on 16/03/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class SaloonViewModel(
    private val repository: SaloonRepository
) : BaseViewModel(repository) {

    private val _getSaloon: MutableLiveData<Resource<GetSaloonResponse>> = SingleEventLiveData()
    val getSaloon: LiveData<Resource<GetSaloonResponse>> get() = _getSaloon
    private val _getSaloonDetails: MutableLiveData<Resource<GetSaloonDetailResponse>> =
        SingleEventLiveData()
    val getSaloonDetails: LiveData<Resource<GetSaloonDetailResponse>> get() = _getSaloonDetails

    private val _addSaloon: MutableLiveData<Resource<AddSaloonResponse>> = SingleEventLiveData()
    val addSaloon: LiveData<Resource<AddSaloonResponse>> get() = _addSaloon

    private val _addHoliday: MutableLiveData<Resource<AddHolidayResponse>> = SingleEventLiveData()
    val addHoliday: LiveData<Resource<AddHolidayResponse>> get() = _addHoliday

    private val _addHour: MutableLiveData<Resource<AddWorkingHourResponse>> = SingleEventLiveData()
    val addHour: LiveData<Resource<AddWorkingHourResponse>> get() = _addHour
    private val _deleteHoliday: MutableLiveData<Resource<DeleteHolidayResponse>> =
        SingleEventLiveData()
    val deleteHoliday: LiveData<Resource<DeleteHolidayResponse>> get() = _deleteHoliday
    private val _getServiceProviders: MutableLiveData<Resource<GetServiceProviderResponse>> =
        SingleEventLiveData()
    val getServiceProviders: LiveData<Resource<GetServiceProviderResponse>> get() = _getServiceProviders
    private val _getSaloonsSubscriptions: MutableLiveData<Resource<GetSubscriptionsResponse>> =
        SingleEventLiveData()
    val getSaloonsSubscriptions: LiveData<Resource<GetSubscriptionsResponse>> get() = _getSaloonsSubscriptions

    fun getSaloons() = viewModelScope.launch {
        _getSaloon.value = repository.getSaloons()
    }

    fun getServiceProviders() = viewModelScope.launch {
        _getServiceProviders.value = repository.getServiceProviders()
    }

    fun getSaloonsSubscriptions(saloonId: Int) = viewModelScope.launch {
        _getSaloonsSubscriptions.value = repository.getSaloonsSubscriptions(saloonId)
    }

    fun getSaloons(saloonId: Int) = viewModelScope.launch {
        _getSaloonDetails.value = repository.getSaloons(saloonId)
    }

    fun addSaloon(
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
    ) = viewModelScope.launch {
        _addSaloon.value = repository.addSaloon(
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

    fun updateSaloon(
        saloonId: Int, requestData: UpdateSaloonRequest
    ) = viewModelScope.launch {
        _addSaloon.value = repository.updateSaloon(
            saloonId,
            requestData
        )
    }

    fun addHoliday(saloonId: Int, request: AddHolidayRequest) = viewModelScope.launch {
        _addHoliday.value = repository.addHoliday(saloonId, request)
    }

    fun addHour(saloonId: Int, request: AddWorkingHourRequest) = viewModelScope.launch {
        _addHour.value = repository.addHour(saloonId, request)
    }

    fun deleteHoliday(saloonId: Int, holidayId: Int) = viewModelScope.launch {
        _deleteHoliday.value = repository.deleteHoliday(saloonId, holidayId)
    }

    fun deleteWorkingHour(saloonId: Int, day: String) = viewModelScope.launch {
        _deleteHoliday.value = repository.deleteWorkingHour(saloonId, day)
    }
}