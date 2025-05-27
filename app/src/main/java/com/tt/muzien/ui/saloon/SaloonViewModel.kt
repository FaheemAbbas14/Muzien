package com.tt.muzien.ui.saloon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.MemberRepository
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.data.requests.AddHolidayRequest
import com.tt.muzien.data.requests.AddSubscriptionRequest
import com.tt.muzien.data.requests.AddWorkingHourRequest
import com.tt.muzien.data.requests.UpdateSaloonRequest
import com.tt.muzien.data.responses.AddHolidayResponse
import com.tt.muzien.data.responses.AddMemberDataResponse
import com.tt.muzien.data.responses.AddSaloonResponse
import com.tt.muzien.data.responses.AddSubscribtionResponse
import com.tt.muzien.data.responses.AddWorkingHourResponse
import com.tt.muzien.data.responses.DeleteHolidayResponse
import com.tt.muzien.data.responses.GetPlansResponse
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
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class SaloonViewModel(
    private val repository: SaloonRepository,
) : BaseViewModel(repository) {
    private var memberRepository: MemberRepository? = null
    fun setMemberRepo(memberRepo: MemberRepository) {
        memberRepository = memberRepo
    }

    private val _getSaloon: MutableLiveData<Resource<GetSaloonResponse>> = SingleEventLiveData()
    val getSaloon: LiveData<Resource<GetSaloonResponse>> get() = _getSaloon

    private val _getSaloonDetails: MutableLiveData<Resource<GetSaloonDetailResponse>> =
        SingleEventLiveData()
    val getSaloonDetails: LiveData<Resource<GetSaloonDetailResponse>> get() = _getSaloonDetails

    private val _addSaloon: MutableLiveData<Resource<AddSaloonResponse>> = SingleEventLiveData()
    val addSaloon: LiveData<Resource<AddSaloonResponse>> get() = _addSaloon

    private val _uploadSaloonCertificate: MutableLiveData<Resource<AddSaloonResponse>> =
        SingleEventLiveData()
    val uploadSaloonCertificate: LiveData<Resource<AddSaloonResponse>> get() = _uploadSaloonCertificate
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

    private val _getPlans: MutableLiveData<Resource<GetPlansResponse>> =
        SingleEventLiveData()
    val getPlans: LiveData<Resource<GetPlansResponse>> get() = _getPlans


    private val _addSubscriptions: MutableLiveData<Resource<AddSubscribtionResponse>> =
        SingleEventLiveData()
    val addSubscriptions: LiveData<Resource<AddSubscribtionResponse>> get() = _addSubscriptions
    private val _addMemberData: MutableLiveData<Resource<AddMemberDataResponse>> =
        SingleEventLiveData()
    val addMemberData: LiveData<Resource<AddMemberDataResponse>> get() = _addMemberData


    fun getSaloons(page: Int? = null) = viewModelScope.launch {
        _getSaloon.value = repository.getSaloons(page)
    }

    fun getSaloonsDetails(saloonId: Int? = null) = viewModelScope.launch {
        _getSaloonDetails.value = repository.getSaloonsDetail(saloonId)
    }

    fun getServiceProviders(isActive: Boolean, isAdmin: Boolean?) = viewModelScope.launch {
        _getServiceProviders.value = memberRepository?.getServiceProvider(isActive, isAdmin)
    }

    fun getSaloonsSubscriptions(saloonId: Int? = null) = viewModelScope.launch {
        _getSaloonsSubscriptions.value = repository.getSaloonsSubscriptions(saloonId)
    }

    fun getPlans(
        name: String? = null,
        isActive: Boolean? = null,
        days: Int? = null,
    ) = viewModelScope.launch {
        _getPlans.value = repository.getPlans(name, isActive, days)
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
        holidays: MutableMap<String, RequestBody>,
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
        saloonId: Int, requestData: UpdateSaloonRequest,
    ) = viewModelScope.launch {
        _addSaloon.value = repository.updateSaloon(
            saloonId,
            requestData
        )
    }

    fun uploadSaloonCertificate(
        saloonId: Int, requestData: MultipartBody.Part?,
    ) = viewModelScope.launch {
        _uploadSaloonCertificate.value = repository.uploadSaloonCertificate(
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

    fun addSubscriptions(saloonId: Int, request: AddSubscriptionRequest) = viewModelScope.launch {
        _addSubscriptions.value = repository.addSubscriptions(saloonId, request)
    }

    fun updateSubscriptions(saloonId: Int, subscriptionId: Int, request: AddSubscriptionRequest) =
        viewModelScope.launch {
            _addSubscriptions.value =
                repository.updateSubscriptions(saloonId, subscriptionId, request)
        }

    fun addMemberHoliday(userId: Int, request: AddHolidayRequest) = viewModelScope.launch {
        _addMemberData.value = memberRepository?.addHoliday(userId, request)
    }

    fun addMemberWorkingHour(userId: Int, request: AddWorkingHourRequest) = viewModelScope.launch {
        _addMemberData.value = memberRepository?.addWorkingHour(userId, request)
    }
}