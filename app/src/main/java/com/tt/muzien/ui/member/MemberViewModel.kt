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
import com.tt.muzien.data.requests.AddSaloonServiceRequest
import com.tt.muzien.data.requests.AddServiceSaloonRequest
import com.tt.muzien.data.requests.AddWorkingHourRequest
import com.tt.muzien.data.responses.AddMemberDataResponse
import com.tt.muzien.data.responses.AddMemberResponse
import com.tt.muzien.data.responses.AddServiceResponse
import com.tt.muzien.data.responses.GetCategoriesResponse
import com.tt.muzien.data.responses.GetMemberDetails
import com.tt.muzien.data.responses.GetMembersResponse
import com.tt.muzien.data.responses.GetSaloonResponse
import com.tt.muzien.data.responses.GetServicesResponse
import com.tt.muzien.data.responses.GetsUsersResponse
import com.tt.muzien.data.responses.SaloonServiceResponse
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
    private val repository: MemberRepository
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

    private val _inActiveMember: MutableLiveData<Resource<Unit>> = SingleEventLiveData()
    val inActiveMember: LiveData<Resource<Unit>> get() = _inActiveMember
    private val _deleteMember: MutableLiveData<Resource<Unit>> = SingleEventLiveData()
    val deleteMember: LiveData<Resource<Unit>> get() = _deleteMember

    private val _makeManger: MutableLiveData<Resource<Unit>> = SingleEventLiveData()
    val makeManger: LiveData<Resource<Unit>> get() = _makeManger

    private val _getCategories: MutableLiveData<Resource<GetCategoriesResponse>> =
        SingleEventLiveData()
    val getCategories: LiveData<Resource<GetCategoriesResponse>> get() = _getCategories
    private val _getSaloonCategories: MutableLiveData<Resource<GetServicesResponse>> =
        SingleEventLiveData()
    val getSaloonCategories: LiveData<Resource<GetServicesResponse>> get() = _getSaloonCategories

    private val _addMemberData: MutableLiveData<Resource<AddMemberDataResponse>> =
        SingleEventLiveData()
    val addMemberData: LiveData<Resource<AddMemberDataResponse>> get() = _addMemberData

    private val _removeHoliday: MutableLiveData<Resource<AddMemberDataResponse>> =
        SingleEventLiveData()
    val removeHoliday: LiveData<Resource<AddMemberDataResponse>> get() = _removeHoliday

    private val _addSaloonService: MutableLiveData<Resource<AddServiceResponse>> =
        SingleEventLiveData()
    val addSaloonService: LiveData<Resource<AddServiceResponse>> get() = _addSaloonService

    private val _removeWorkingHour: MutableLiveData<Resource<AddMemberDataResponse>> =
        SingleEventLiveData()
    val removeWorkingHour: LiveData<Resource<AddMemberDataResponse>> get() = _removeWorkingHour

    fun getUsers() = viewModelScope.launch {
        _getUsers.value = userRepository?.getUsers()
    }

    fun getSaloons() = viewModelScope.launch {
        _getSaloon.value = saloonRepository?.getSaloons()
    }

    fun getMembers() = viewModelScope.launch {
        _getMembers.value = repository.getMembers()
    }

    fun getMemberDetails(memberId: Int) = viewModelScope.launch {
        _getMemberDetails.value = repository.getMemberDetails(memberId)
    }

    fun getMembers(saloonId: String) = viewModelScope.launch {
        _getMembers.value = repository.getMembers(saloonId)
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

    fun getCategories() = viewModelScope.launch {
        _getCategories.value = serviceRepository?.getCategories()
    }
    fun getCategories(saloonIds: String) = viewModelScope.launch {
        _getSaloonCategories.value = serviceRepository?.getCategories(saloonIds)
    }


    fun addService(userId: Int, request: AddMemberService) = viewModelScope.launch {
        _addMemberData.value = repository.addService(userId, request)
    }

    fun addHoliday(userId: Int, request: AddHolidayRequest) = viewModelScope.launch {
        _addMemberData.value = repository.addHoliday(userId, request)
    }

    fun addWorkingHour(userId: Int, request: AddWorkingHourRequest) = viewModelScope.launch {
        _addMemberData.value = repository.addWorkingHour(userId, request)
    }

    fun removeWorkingHour(userId: Int, request: String) = viewModelScope.launch {
        _removeWorkingHour.value = repository.removeWorkingHour(userId, request)
    }

    fun removeHoliday(userId: Int, request: Int) = viewModelScope.launch {
        _removeHoliday.value = repository.removeHoliday(userId, request)
    }
    fun addSaloonService(request: AddServiceSaloonRequest) = viewModelScope.launch {
        _addSaloonService.value = serviceRepository?.addSaloonService( request)
    }
}