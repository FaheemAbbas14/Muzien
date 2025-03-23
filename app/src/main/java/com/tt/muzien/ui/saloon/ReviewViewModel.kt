package com.tt.muzien.ui.saloon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.ReviewRepository
import com.tt.muzien.data.responses.GetReviewsResponse
import com.tt.muzien.ui.base.BaseViewModel
import com.tt.muzien.utilities.SingleEventLiveData
import kotlinx.coroutines.launch


/**
 * Created by Faheem Abbas on 22/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class ReviewViewModel(
    private val repository: ReviewRepository
) : BaseViewModel(repository) {
    private val _getReviews: MutableLiveData<Resource<GetReviewsResponse>> = SingleEventLiveData()
    val getReviews: LiveData<Resource<GetReviewsResponse>> get() = _getReviews
    fun getReviews(saloonId: String? = null, reviewId: String? = null) = viewModelScope.launch {
        _getReviews.value = repository.getReviews(saloonId, reviewId)
    }
}