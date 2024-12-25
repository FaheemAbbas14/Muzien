package com.tt.muzien.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tt.muzien.data.dto.FilterData
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.data.repository.UserRepository
import com.tt.muzien.data.responses.LoginResponse
import com.tt.muzien.ui.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel controlling Logged-in user experience between [HomeFragment] and [UserRepository]
 *
 * @property repository [UserRepository] class to persist user information
 */
class HomeViewModel(
  private val repository: HomeRepository
): BaseViewModel(repository) {

  private val _user: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
  val user: LiveData<Resource<LoginResponse>>
  get() = _user
  val sharedData = MutableLiveData<FilterData>()

}