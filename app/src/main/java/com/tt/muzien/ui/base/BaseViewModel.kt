package com.tt.muzien.ui.base

import androidx.lifecycle.ViewModel
import com.tt.muzien.data.network.UserApi
import com.tt.muzien.data.repository.BaseRepository

abstract class BaseViewModel(private val repository:BaseRepository): ViewModel() {

  suspend fun logout(api : UserApi) = repository.logout(api)

}