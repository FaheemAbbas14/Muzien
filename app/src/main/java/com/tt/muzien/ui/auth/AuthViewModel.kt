package com.tt.muzien.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.data.requests.LoginRequest
import com.tt.muzien.data.requests.RegisterRequest
import com.tt.muzien.data.requests.UpdateUser
import com.tt.muzien.data.requests.VerifyOTPRequest
import com.tt.muzien.data.responses.LoginResponse
import com.tt.muzien.data.responses.MyResponse
import com.tt.muzien.data.responses.RegisterResponse
import com.tt.muzien.data.responses.UpdateUserResponse
import com.tt.muzien.data.responses.VerifyLoginResponse
import com.tt.muzien.ui.base.BaseViewModel
import com.tt.muzien.utilities.SingleEventLiveData
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class AuthViewModel(
    private val repository: AuthRepository
) : BaseViewModel(repository) {

    private val _login: MutableLiveData<Resource<LoginResponse>> = SingleEventLiveData()
    val login: LiveData<Resource<LoginResponse>> get() = _login
    private val _register: MutableLiveData<Resource<RegisterResponse>> = SingleEventLiveData()
    val register: LiveData<Resource<RegisterResponse>> get() = _register

    private val _verifyLogin: MutableLiveData<Resource<VerifyLoginResponse>> = SingleEventLiveData()
    val verifyLogin: LiveData<Resource<VerifyLoginResponse>> get() = _verifyLogin

    private val _my: MutableLiveData<Resource<MyResponse>> = SingleEventLiveData()
    val my: LiveData<Resource<MyResponse>> get() = _my


    private val _updateUser: MutableLiveData<Resource<UpdateUserResponse>> = SingleEventLiveData()
    val updateUser: LiveData<Resource<UpdateUserResponse>> get() = _updateUser

    private val _uploadPhoto: MutableLiveData<Resource<MyResponse>> = SingleEventLiveData()
    val uploadPhoto: LiveData<Resource<MyResponse>> get() = _uploadPhoto

    fun sendOTP(request: LoginRequest) = viewModelScope.launch {
        _login.value = repository.sendOTP(request)
    }

    fun register(request: RegisterRequest) = viewModelScope.launch {
        _register.value = repository.register(request)
    }

    fun verifyLogin(request: VerifyOTPRequest) = viewModelScope.launch {
        _verifyLogin.value = repository.verifyLogin(request)
    }

    fun my(request: Long) = viewModelScope.launch {
        _my.value = repository.my(request)
    }

    fun updateUser(request: UpdateUser) = viewModelScope.launch {
        _updateUser.value = repository.updateUser(request)
    }

    fun uploadImage(request: MultipartBody.Part) = viewModelScope.launch {
        _uploadPhoto.value = repository.uploadImage(request)
    }

    suspend fun saveAuthToken(token: String) {
        repository.saveAuthToken(token)
    }

}