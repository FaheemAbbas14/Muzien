package com.tt.muzien.ui.member

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.MemberRepository
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.data.repository.ServiceRepository
import com.tt.muzien.data.repository.UserRepository
import com.tt.muzien.data.requests.AddHolidayRequest
import com.tt.muzien.data.requests.AddMemberRequest
import com.tt.muzien.data.requests.AddMemberService
import com.tt.muzien.data.requests.AddServiceSaloonRequest
import com.tt.muzien.data.requests.AddWorkingHourRequest
import com.tt.muzien.data.responses.AddMemberDataResponse
import com.tt.muzien.data.responses.AddMemberResponse
import com.tt.muzien.data.responses.AddServiceResponse
import com.tt.muzien.data.responses.GetCategoriesResponse
import com.tt.muzien.data.responses.GetMemberDetails
import com.tt.muzien.data.responses.GetMembersResponse
import com.tt.muzien.data.responses.GetSaloonResponse
import com.tt.muzien.data.responses.GetServiceProviderResponse
import com.tt.muzien.data.responses.GetServicesResponse
import com.tt.muzien.data.responses.GetUserHolidaysResponse
import com.tt.muzien.data.responses.GetsUsersResponse
import com.tt.muzien.data.responses.MarkManagerResponse
import com.tt.muzien.data.responses.UserServicesResponse
import com.tt.muzien.ui.base.BaseViewModel
import com.tt.muzien.utilities.SingleEventLiveData
import kotlinx.coroutines.launch


/**
 * Created by Faheem Abbas on 15/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class MemberViewModel(
    private val repository: MemberRepository,
) : BaseViewModel(repository) {
    private var saloonRepository: SaloonRepository? = null
    private var userRepository: UserRepository? = null
    private var serviceRepository: ServiceRepository? = null
    fun setSaloonRepo(saloonRepo: SaloonRepository) {
        saloonRepository = saloonRepo
    }

    fun setUserRepo(userRepo: UserRepository) {
        userRepository = userRepo
    }

    fun setServiceRepo(serviceRepo: ServiceRepository) {
        serviceRepository = serviceRepo
    }

    private val _getUsers: MutableLiveData<Resource<GetsUsersResponse>> = SingleEventLiveData()
    val getUsers: LiveData<Resource<GetsUsersResponse>> get() = _getUsers

    private val _getSaloon: MutableLiveData<Resource<GetSaloonResponse>> = SingleEventLiveData()
    val getSaloon: LiveData<Resource<GetSaloonResponse>> get() = _getSaloon

    private val _getMembers: MutableLiveData<Resource<GetMembersResponse>> = SingleEventLiveData()
    val getMembers: LiveData<Resource<GetMembersResponse>> get() = _getMembers
    private val _getMemberDetails: MutableLiveData<Resource<GetMemberDetails>> =
        SingleEventLiveData()
    val getMemberDetails: LiveData<Resource<GetMemberDetails>> get() = _getMemberDetails
    private val _sendInvite: MutableLiveData<Resource<AddMemberResponse>> = SingleEventLiveData()
    val sendInvite: LiveData<Resource<AddMemberResponse>> get() = _sendInvite
    private val _removeInvite: MutableLiveData<Resource<MarkManagerResponse>> =
        SingleEventLiveData()
    val removeInvite: LiveData<Resource<MarkManagerResponse>> get() = _removeInvite

    private val _inActiveMember: MutableLiveData<Resource<MarkManagerResponse>> =
        SingleEventLiveData()
    val inActiveMember: LiveData<Resource<MarkManagerResponse>> get() = _inActiveMember
    private val _deleteMember: MutableLiveData<Resource<MarkManagerResponse>> =
        SingleEventLiveData()
    val deleteMember: LiveData<Resource<MarkManagerResponse>> get() = _deleteMember

    private val _makeManger: MutableLiveData<Resource<MarkManagerResponse>> = SingleEventLiveData()
    val makeManger: LiveData<Resource<MarkManagerResponse>> get() = _makeManger
    private val _removeManger: MutableLiveData<Resource<MarkManagerResponse>> =
        SingleEventLiveData()
    val removeManger: LiveData<Resource<MarkManagerResponse>> get() = _removeManger

    private val _getCategories: MutableLiveData<Resource<GetCategoriesResponse>> =
        SingleEventLiveData()
    val getCategories: LiveData<Resource<GetCategoriesResponse>> get() = _getCategories
    private val _getSaloonCategories: MutableLiveData<Resource<GetServicesResponse>> =
        SingleEventLiveData()
    val getSaloonCategories: LiveData<Resource<GetServicesResponse>> get() = _getSaloonCategories

    private val _addMemberData: MutableLiveData<Resource<AddMemberDataResponse>> =
        SingleEventLiveData()
    val addMemberData: LiveData<Resource<AddMemberDataResponse>> get() = _addMemberData
    private val _getUserHolidays: MutableLiveData<Resource<GetUserHolidaysResponse>> =
        SingleEventLiveData()
    val getUserHolidays: LiveData<Resource<GetUserHolidaysResponse>> get() = _getUserHolidays

    private val _getUserServices: MutableLiveData<Resource<UserServicesResponse>> =
        SingleEventLiveData()
    val getUserServices: LiveData<Resource<UserServicesResponse>> get() = _getUserServices
    private val _remove: MutableLiveData<Resource<AddMemberDataResponse>> =
        SingleEventLiveData()
    val remove: LiveData<Resource<AddMemberDataResponse>> get() = _remove

    private val _addSaloonService: MutableLiveData<Resource<AddServiceResponse>> =
        SingleEventLiveData()
    val addSaloonService: LiveData<Resource<AddServiceResponse>> get() = _addSaloonService

    private val _removeWorkingHour: MutableLiveData<Resource<AddMemberDataResponse>> =
        SingleEventLiveData()
    val removeWorkingHour: LiveData<Resource<AddMemberDataResponse>> get() = _removeWorkingHour
    private val _getServiceProviders: MutableLiveData<Resource<GetServiceProviderResponse>> =
        SingleEventLiveData()
    val getServiceProviders: LiveData<Resource<GetServiceProviderResponse>> get() = _getServiceProviders

    fun getUsers() = viewModelScope.launch {
        _getUsers.value = userRepository?.getUsers()
    }

    fun getSaloons() = viewModelScope.launch {
        _getSaloon.value = saloonRepository?.getSaloons()
    }

    fun getMembers(isActive: Boolean? = null, status: Int? = null) = viewModelScope.launch {
        _getMembers.value = repository.getMembers(isActive, status)
    }

    fun getMemberDetails(memberId: Int) = viewModelScope.launch {
        _getMemberDetails.value = repository.getMemberDetails(memberId)
    }

    fun getMembers(
        saloonId: String,
        isActive: Boolean? = null,
        status: Int? = null,
    ) = viewModelScope.launch {
        _getMembers.value = repository.getMembers(saloonId, isActive, status)
    }

    fun sendInvite(request: AddMemberRequest) = viewModelScope.launch {
        _sendInvite.value = repository.sendInvite(request)
    }

    fun inActiveMember(memberId: Int) = viewModelScope.launch {
        _inActiveMember.value = repository.inActiveMember(memberId)
    }

    fun deleteMember(memberId: Int) = viewModelScope.launch {
        _deleteMember.value = repository.deleteMember(memberId)
    }

    fun makeManager(memberId: Int) = viewModelScope.launch {
        _makeManger.value = repository.makeManager(memberId)
    }

    fun removeManager(memberId: Int) = viewModelScope.launch {
        _removeManger.value = repository.removeManager(memberId)
    }

    fun removeInvite(
        inviteId: Int,
    ) = viewModelScope.launch {
        _removeInvite.value = repository.removeInvite(inviteId)
    }

    fun getCategories() = viewModelScope.launch {
        _getCategories.value = serviceRepository?.getCategories()
    }
    fun getAllCategories() = viewModelScope.launch {
        _getCategories.value = serviceRepository?.getAllCategories()
    }
    fun getCategories(saloonIds: Int) = viewModelScope.launch {
        _getSaloonCategories.value = serviceRepository?.getCategories(saloonIds)
    }


    fun addService(userId: Int, request: AddMemberService) = viewModelScope.launch {
        _addMemberData.value = repository.addService(userId, request)
    }

    fun addHoliday(userId: Int, request: AddHolidayRequest) = viewModelScope.launch {
        _addMemberData.value = repository.addHoliday(userId, request)
    }

    fun getUserHolidays(userId: Int) = viewModelScope.launch {
        _getUserHolidays.value = repository.getUserHolidays(userId)
    }
    fun getUserServices(userId: Int) = viewModelScope.launch {
        _getUserServices.value = repository.getUserServices(userId)
    }
    fun addWorkingHour(userId: Int, request: AddWorkingHourRequest) = viewModelScope.launch {
        _addMemberData.value = repository.addWorkingHour(userId, request)
    }

    fun removeWorkingHour(userId: Int, request: String) = viewModelScope.launch {
        _removeWorkingHour.value = repository.removeWorkingHour(userId, request)
    }

    fun removeHoliday(userId: Int, request: Int) = viewModelScope.launch {
        _remove.value = repository.removeHoliday(userId, request)
    }

    fun deleteService(userId: Int, serviceId: Int) = viewModelScope.launch {
        _remove.value = repository.deleteService(userId, serviceId)
    }

    fun getServiceProvider(isActive: Boolean, isAdmin: Boolean) = viewModelScope.launch {
        _getServiceProviders.value = repository.getServiceProvider(isActive, isAdmin)
    }

    fun addSaloonService(request: AddServiceSaloonRequest) = viewModelScope.launch {
        _addSaloonService.value = serviceRepository?.addSaloonService(request)
    }
}