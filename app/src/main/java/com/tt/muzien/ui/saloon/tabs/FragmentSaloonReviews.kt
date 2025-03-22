package com.tt.muzien.ui.saloon.tabs

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.data.dto.ReviewsInfo
import com.tt.muzien.data.dto.SaloonDto
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.ReviewApi
import com.tt.muzien.data.repository.ReviewRepository
import com.tt.muzien.databinding.FragmentSaloonReviewsBinding
import com.tt.muzien.ui.adapters.ReviewsListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.saloon.ReviewViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.TimeHelper
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentSaloonReviews :
    BaseFragment<ReviewViewModel, FragmentSaloonReviewsBinding, ReviewRepository>() {
    private val reviewsList = arrayListOf<ReviewsInfo>()
    var selectedSaloon: SaloonDto? = null
    var avgRating: Double = 0.0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getReviews()
    }

    private fun setReviewsAdopter() {
        binding.txtRatings.text =
            "$avgRating (${reviewsList.size}${if (reviewsList.size == 1) " review" else " reviews"})"
        //  binding.txtHeading.text = "Members(${membersList.size})"
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
//                var nextFragment = FragmentPlaceDetails()
//                nextFragment.itemId = featuredItemsList[position].id
//                nextFragment.placeType = EnumItemListType.Featured
//                (activity as DashboardActivity?)?.loadFragment(nextFragment)

            }
        }
        binding.rcyReviews.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rcyReviews.adapter =
            ReviewsListAdapter(
                reviewsList,
                requireContext()
            )
    }

    override fun getViewModel(): Class<ReviewViewModel> {
        return ReviewViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSaloonReviewsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        ReviewRepository(remoteDataSource.buildApi(ReviewApi::class.java, requireContext()))

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getReviews() {
        viewModel.getReviews.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        reviewsList.clear()
                        var totalRating = 0.0
                        for (review in it.value.data.reviews) {
                            totalRating += review.rating
                            val photos = arrayListOf<String>()
                            for (image in review.images) {
                                photos.add("")
                            }

                            reviewsList.add(
                                ReviewsInfo(
                                    review.id.toString(),
                                    review.reviewer.picture,
                                    review.reviewer.fullName,
                                    "Added on: ${
                                        TimeHelper.convertISOToDate(
                                            review.createdAt,
                                            "dd/MM/yyyy"
                                        )
                                    }",
                                    "Service Provider: John",
                                    review.rating.toString(),
                                    review.comment,
                                    photos
                                )
                            )
                        }
                        avgRating = totalRating / it.value.data.reviews.size
                        setReviewsAdopter()
                    } else {
                        requireView().snackbar(it.value.message)
                    }
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())

                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.getReviews(selectedSaloon?.id.toString())
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
}