package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tt.muzien.data.dto.ReviewsInfo
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentSaloonReviewsBinding
import com.tt.muzien.ui.adapters.ReviewsListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeViewModel
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentSaloonReviews :
    BaseFragment<HomeViewModel, FragmentSaloonReviewsBinding, HomeRepository>() {
    private val reviewsList = arrayListOf<ReviewsInfo>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setReviewsAdopter()
    }

    private fun setReviewsAdopter() {
        reviewsList.clear()
        for (i in 1..10) {
            val photos = arrayListOf<String>()
            for (j in 1..10) {
                photos.add("")
            }
            println("Index: $i")
            reviewsList.add(
                ReviewsInfo(
                    "",
                    "",
                    "Peter Smith",
                    "Added on: 12/12/2023",
                    "Service Provider: John",
                    "4.5",
                    "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text",
                    photos
                )
            )
        }
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

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSaloonReviewsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java,requireContext()), userPreferences)

}