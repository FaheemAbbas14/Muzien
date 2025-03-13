package com.tt.muzien.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.data.requests.LoginRequest
import com.tt.muzien.data.requests.RegisterRequest
import com.tt.muzien.data.requests.VerifyOTPRequest
import com.tt.muzien.data.responses.LoginResponse
import com.tt.muzien.data.responses.RegisterResponse
import com.tt.muzien.data.responses.VerifyLoginResponse
import com.tt.muzien.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : BaseViewModel(repository) {

    private val _login: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val login: LiveData<Resource<LoginResponse>> get() = _login
    private val _register: MutableLiveData<Resource<RegisterResponse>> = MutableLiveData()
    val register: LiveData<Resource<RegisterResponse>> get() = _register

    private val _verifyLogin: MutableLiveData<Resource<VerifyLoginResponse>> = MutableLiveData()
    val verifyLogin: LiveData<Resource<VerifyLoginResponse>> get() = _verifyLogin

    fun sendOTP(request: LoginRequest) = viewModelScope.launch {
        _login.value = repository.sendOTP(request)
    }

    fun register(request: RegisterRequest) = viewModelScope.launch {
        _register.value = repository.register(request)
    }

    fun verifyLogin(request: VerifyOTPRequest) = viewModelScope.launch {
        _verifyLogin.value = repository.verifyLogin(request)
    }

    suspend fun saveAuthToken(token: String) {
        repository.saveAuthToken(token)
    }

}