package com.tt.muzien.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.data.responses.LoginResponse
import com.tt.muzien.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class AuthViewModel(
  private val repository: AuthRepository
): BaseViewModel(repository) {

  private val _loginResponse : MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
  val loginResponse: LiveData<Resource<LoginResponse>> get() = _loginResponse

  fun login(
    email: String,
    password: String) = viewModelScope.launch {
      _loginResponse.value = repository.login(email, password)
  }

  suspend fun saveAuthToken(token: String) {
    repository.saveAuthToken(token)
  }

}