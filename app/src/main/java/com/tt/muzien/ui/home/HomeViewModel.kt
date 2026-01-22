package com.tt.muzien.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.data.repository.UserRepository
import com.tt.muzien.data.requests.LoginRequest
import com.tt.muzien.data.requests.UpdateUser
import com.tt.muzien.data.requests.VerifyOTPRequest
import com.tt.muzien.data.responses.GetAnalyticsResponse
import com.tt.muzien.data.responses.GetBookingResponse
import com.tt.muzien.data.responses.GetNotificationsResponse
import com.tt.muzien.data.responses.GetRevenueResponse
import com.tt.muzien.data.responses.LoginResponse
import com.tt.muzien.data.responses.MyResponse
import com.tt.muzien.data.responses.UpdateUserResponse
import com.tt.muzien.data.responses.VerifyLoginResponse
import com.tt.muzien.ui.base.BaseViewModel
import com.tt.muzien.utilities.SingleEventLiveData
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

/**
 * ViewModel controlling Logged-in user experience between [HomeFragment] and [UserRepository]
 *
 * @property repository [UserRepository] class to persist user information
 */
class HomeViewModel(
    private val repository: HomeRepository,
) : BaseViewModel(repository) {
    private var userRepository: UserRepository? = null
    private val _user: MutableLiveData<Resource<LoginResponse>> = SingleEventLiveData()
    val user: LiveData<Resource<LoginResponse>>
        get() = _user
    private val _uploadPhoto: MutableLiveData<Resource<MyResponse>> = SingleEventLiveData()
    val uploadPhoto: LiveData<Resource<MyResponse>> get() = _uploadPhoto

    private val _sendOtp: MutableLiveData<Resource<LoginResponse>> = SingleEventLiveData()
    val sendOtp: LiveData<Resource<LoginResponse>> get() = _sendOtp

    private val _verifyLogin: MutableLiveData<Resource<VerifyLoginResponse>> = SingleEventLiveData()
    val verifyLogin: LiveData<Resource<VerifyLoginResponse>> get() = _verifyLogin

    private val _my: MutableLiveData<Resource<MyResponse>> = SingleEventLiveData()
    val my: LiveData<Resource<MyResponse>> get() = _my
    private val _updateUser: MutableLiveData<Resource<UpdateUserResponse>> = SingleEventLiveData()
    val updateUser: LiveData<Resource<UpdateUserResponse>> get() = _updateUser

    private val _logout: MutableLiveData<Resource<LoginResponse>> = SingleEventLiveData()
    val logout: LiveData<Resource<LoginResponse>> get() = _logout
    private val _deleteUser: MutableLiveData<Resource<Unit>> = SingleEventLiveData()
    val deleteUser: LiveData<Resource<Unit>> get() = _deleteUser
    private val _getNotifications: MutableLiveData<Resource<GetNotificationsResponse>> =
        SingleEventLiveData()
    val getNotifications: LiveData<Resource<GetNotificationsResponse>> get() = _getNotifications

    private val _getAnalytics: MutableLiveData<Resource<GetAnalyticsResponse>> =
        SingleEventLiveData()
    val getAnalytics: LiveData<Resource<GetAnalyticsResponse>> get() = _getAnalytics

    private val _getWeeklyRevenue: MutableLiveData<Resource<GetRevenueResponse>> =
        SingleEventLiveData()
    val getWeeklyRevenue: LiveData<Resource<GetRevenueResponse>> get() = _getWeeklyRevenue

    private val _getMonthlyRevenue: MutableLiveData<Resource<GetRevenueResponse>> =
        SingleEventLiveData()
    val getMonthlyRevenue: LiveData<Resource<GetRevenueResponse>> get() = _getMonthlyRevenue

    fun setUserRepo(userRepo: UserRepository) {
        userRepository = userRepo
    }

    fun uploadImage(request: MultipartBody.Part) = viewModelScope.launch {
        _uploadPhoto.value = userRepository?.uploadImage(request)
    }

    fun sendOTP(request: LoginRequest) = viewModelScope.launch {
        _sendOtp.value = userRepository?.sendOTP(request)
    }

    fun verifyLogin(request: VerifyOTPRequest) = viewModelScope.launch {
        _verifyLogin.value = userRepository?.verifyLogin(request)
    }

    fun my(request: Long) = viewModelScope.launch {
        _my.value = userRepository?.my(request)
    }

    fun updateUser(request: UpdateUser) = viewModelScope.launch {
        _updateUser.value = userRepository?.updateUser(request)
    }

    fun logout() = viewModelScope.launch {
        _logout.value = userRepository?.logout()
    }

    fun deleteUser() = viewModelScope.launch {
        _deleteUser.value = userRepository?.deleteUser()
    }

    private val _getBooking: MutableLiveData<Resource<GetBookingResponse>> = SingleEventLiveData()
    val getBooking: LiveData<Resource<GetBookingResponse>> get() = _getBooking
    fun getAnalytics(
        saloonIds: String? = null,
        startDate: String? = null,
        endDate: String? = null,
    ) = viewModelScope.launch {
        _getAnalytics.value = repository.getAnalytics(saloonIds, startDate, endDate)
    }

    fun getWeeklyRevenue(
        saloonIds: String? = null,
        startDate: String? = null,
        endDate: String? = null,
    ) = viewModelScope.launch {
        _getWeeklyRevenue.value = repository.getWeeklyRevenue(saloonIds, startDate, endDate)
    }

    fun getMonthlyRevenue(
        saloonIds: String? = null,
        startDate: String? = null,
        endDate: String? = null,
    ) = viewModelScope.launch {
        _getMonthlyRevenue.value = repository.getMonthlyRevenue(saloonIds, startDate, endDate)
    }

    fun getNotifications(tzOffset: Int,page: Int,limit: Int) = viewModelScope.launch {
        _getNotifications.value = userRepository?.getNotifications(tzOffset,page,limit)
    }

    fun getNotificationsByType(type: String) = viewModelScope.launch {
        _getNotifications.value = userRepository?.getNotificationsByType(type)
    }
}