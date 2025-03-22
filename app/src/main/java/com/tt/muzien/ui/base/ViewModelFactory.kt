package com.tt.muzien.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tt.muzien.data.repository.AuthRepository
import com.tt.muzien.data.repository.BaseRepository
import com.tt.muzien.data.repository.BookingRepository
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.data.repository.MemberRepository
import com.tt.muzien.data.repository.ReviewRepository
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.data.repository.ServiceRepository
import com.tt.muzien.ui.auth.AuthViewModel
import com.tt.muzien.ui.bookings.BookingViewModel
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.ui.member.MemberViewModel
import com.tt.muzien.ui.saloon.ReviewViewModel
import com.tt.muzien.ui.saloon.SaloonViewModel
import com.tt.muzien.ui.service.ServiceViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repository as AuthRepository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository as HomeRepository) as T
            modelClass.isAssignableFrom(ServiceViewModel::class.java) -> ServiceViewModel(repository as ServiceRepository) as T
            modelClass.isAssignableFrom(MemberViewModel::class.java) -> MemberViewModel(repository as MemberRepository) as T
            modelClass.isAssignableFrom(BookingViewModel::class.java) -> BookingViewModel(repository as BookingRepository) as T
            modelClass.isAssignableFrom(SaloonViewModel::class.java) -> SaloonViewModel(repository as SaloonRepository) as T
            modelClass.isAssignableFrom(ReviewViewModel::class.java) -> ReviewViewModel(repository as ReviewRepository) as T
            else -> throw IllegalArgumentException("Class Not found")
        }

    }
}