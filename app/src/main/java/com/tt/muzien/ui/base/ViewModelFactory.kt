package com.tt.muzien.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.data.repository.BaseRepository
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.data.repository.UserRepository
import com.tt.muzien.ui.auth.AuthViewModel
import com.tt.muzien.ui.home.HomeViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory(
  private val repository: BaseRepository
): ViewModelProvider.NewInstanceFactory() {

  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return when {
      modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repository as AuthRepository) as T
      modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository as HomeRepository) as T
      else -> throw IllegalArgumentException("Class Not found")
    }

  }
}