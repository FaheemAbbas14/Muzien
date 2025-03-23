package com.tt.muzien.ui.saloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tt.muzien.data.dto.SaloonDto
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.SaloonApi
import com.tt.muzien.data.repository.SaloonRepository
import com.tt.muzien.databinding.FragmentSaloonBinding
import com.tt.muzien.ui.adapters.SaloonListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.FragmentFilter
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.FilterSelection
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentSaloon : BaseFragment<SaloonViewModel, FragmentSaloonBinding, SaloonRepository>() {
    private val saloonsList = arrayListOf<SaloonDto>()
    private var fromDate: String = ""
    private var toDate: String = ""
    private var bookingDuration: String = ""
    private var page = 1
    private var totalPage = 1
    private var selection: Int = 0
    private var isLoading: Boolean = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSaloons()
        binding.imgFilter.setOnClickListener {
            var nextFragment = FragmentFilter()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        if (FilterSelection.filterData != null) {
            val selection = FilterSelection.filterData!!.selection
            val fromDateFilter = FilterSelection.filterData!!.from
            val toDateFilter = FilterSelection.filterData!!.to
            if (selection != "") {
                bookingDuration = selection.toString()
                fromDate = fromDateFilter.toString()
                toDate = toDateFilter.toString()
                getSaloons()
            }

        }
    }

    private fun setSaloonAdopter() {

        binding.txtHeading.text = "Salons(${saloonsList.size})"
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
                var nextFragment = FragmentSaloonDetails()
                nextFragment.selectedSaloon = saloonsList[position]
                (activity as HomeActivity?)?.loadFragment(nextFragment)

            }
        }
        binding.rcySaloons.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rcySaloons.adapter =
            SaloonListAdapter(
                saloonsList,
                requireContext(),
                clickListener
            )
        binding.rcySaloons.scrollToPosition(selection)
        binding.rcySaloons.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && totalItemCount > 0) {
                    // Reached the end of the list
                    loadMoreData()
                }
            }
        })

    }

    fun loadMoreData() {
        if (!isLoading && saloonsList.size > 0) {
            if (page < totalPage) {
                page = page + 1
                selection = saloonsList.size - 1
                (activity as HomeActivity?)?.showLoadingIndicator()
                getSaloons()

            }
        }

    }

    override fun getViewModel(): Class<SaloonViewModel> {
        return SaloonViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSaloonBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        SaloonRepository(remoteDataSource.buildApi(SaloonApi::class.java, requireContext()))

    private fun getSaloons() {
        viewModel.getSaloon.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        if (page == 1) {
                            saloonsList.clear()
                        }
                        totalPage = it.value.data.pagination.totalPages.toInt()
                        for (saloon in it.value.data.saloons) {
                            saloonsList.add(
                                SaloonDto(
                                    saloon.id.toInt(),
                                    saloon.SaloonImages,
                                    saloon.name,
                                    saloon.isActive,
                                    saloon.address ?: "",
                                    "${saloon.tRating} (${saloon.numReviews} ${
                                        if (saloon.numReviews.toInt() == 1) "review" else "reviews"
                                    })",
                                    saloon.SaloonWorkHours,
                                    saloon.locationLat.toDouble(), saloon.locationLong.toDouble()
                                )
                            )

                        }
                        setSaloonAdopter()
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
        viewModel.getSaloons(page = page)
        (activity as HomeActivity?)?.showLoadingIndicator()
    }

}