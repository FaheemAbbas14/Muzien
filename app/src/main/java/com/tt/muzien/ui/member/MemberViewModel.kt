package com.tt.muzien.ui.member

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.MemberRepository
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.data.repository.UserRepository
import com.tt.muzien.data.requests.AddMemberRequest
import com.tt.muzien.data.responses.AddMemberResponse
import com.tt.muzien.data.responses.GetMembersResponse
import com.tt.muzien.data.responses.GetSaloonResponse
import com.tt.muzien.data.responses.GetsUsersResponse
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
    fun setSaloonRepo(saloonRepo: SaloonRepository) {
        saloonRepository = saloonRepo
    }

    fun setUserRepo(userRepo: UserRepository) {
        userRepository = userRepo
    }

    private val _getUsers: MutableLiveData<Resource<GetsUsersResponse>> = SingleEventLiveData()
    val getUsers: LiveData<Resource<GetsUsersResponse>> get() = _getUsers

    private val _getSaloon: MutableLiveData<Resource<GetSaloonResponse>> = SingleEventLiveData()
    val getSaloon: LiveData<Resource<GetSaloonResponse>> get() = _getSaloon

    private val _getMembers: MutableLiveData<Resource<GetMembersResponse>> = SingleEventLiveData()
    val getMembers: LiveData<Resource<GetMembersResponse>> get() = _getMembers
    private val _sendInvite: MutableLiveData<Resource<AddMemberResponse>> = SingleEventLiveData()
    val sendInvite: LiveData<Resource<AddMemberResponse>> get() = _sendInvite

    fun getUsers() = viewModelScope.launch {
        _getUsers.value = userRepository?.getUsers()
    }

    fun getSaloons() = viewModelScope.launch {
        _getSaloon.value = saloonRepository?.getSaloons()
    }

    fun getMembers() = viewModelScope.launch {
        _getMembers.value = repository.getMembers()
    }

    fun getMembers(saloonId: String) = viewModelScope.launch {
        _getMembers.value = repository.getMembers(saloonId)
    }

    fun sendInvite(request: AddMemberRequest) = viewModelScope.launch {
        _sendInvite.value = repository.sendInvite(request)
    }

}