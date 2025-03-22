package com.tt.muzien.ui.bookings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.BookingRepository
import com.tt.muzien.data.responses.GetBookingResponse
import com.tt.muzien.ui.base.BaseViewModel
import com.tt.muzien.utilities.SingleEventLiveData
import kotlinx.coroutines.launch


/**
 * Created by Faheem Abbas on 15/03/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class BookingViewModel(
    private val repository: BookingRepository
) : BaseViewModel(repository) {
    private val _getBooking: MutableLiveData<Resource<GetBookingResponse>> = SingleEventLiveData()
    val getBooking: LiveData<Resource<GetBookingResponse>> get() = _getBooking
    fun getBookings(
        saloonIds: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        status: String? = null
    ) = viewModelScope.launch {
        _getBooking.value = repository.getBookings(saloonIds, startDate, endDate, status)
    }
}