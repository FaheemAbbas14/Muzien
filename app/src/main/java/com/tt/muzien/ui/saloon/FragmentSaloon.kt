package com.tt.muzien.ui.saloon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tt.muzien.R
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
import com.tt.muzien.utilities.Appelement
import com.tt.muzien.utilities.FilterSelection
import com.zabihah.ui.ui.interfaces.OnItemClickListner

class FragmentSaloon : BaseFragment<SaloonViewModel, FragmentSaloonBinding, SaloonRepository>() {
    private val saloonsList = arrayListOf<SaloonDto>()
    private var page = 1
    private var totalPage = 1
    private var selection: Int = 0
    private var isLoading: Boolean = false
    private var isActive: Boolean? = null
    var saloonListAdapter: SaloonListAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSaloons(false)
        binding.swipeRefresh.recyclerView = binding.rcySaloons
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            page = 1
            getSaloons(true)
        }
        binding.imgFilter.setOnClickListener {
            var nextFragment = FragmentFilter()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        getSaloons(false)
    }

    private fun setSaloonAdopter() {
        if (saloonsList.isNotEmpty()) {
            binding.rcySaloons.visibility = View.VISIBLE
            binding.llNoDta.visibility = View.GONE
        } else {
            binding.rcySaloons.visibility = View.GONE
            binding.llNoDta.visibility = View.VISIBLE
        }
        binding.txtHeading.text = "${resources.getString(R.string.saloons)}(${saloonsList.size})"
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
                var nextFragment = FragmentSaloonDetails()
                nextFragment.selectedSaloon = saloonsList[position]
                (activity as HomeActivity?)?.loadFragment(nextFragment)

            }
        }
        binding.rcySaloons.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        saloonListAdapter = SaloonListAdapter(
            saloonsList,
            requireContext(),
            clickListener
        )
        binding.rcySaloons.adapter = saloonListAdapter

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
                // (activity as HomeActivity?)?.showLoadingIndicator()
                getSaloons(false)

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

    private fun getSaloons(reload: Boolean) {
        isLoading = true
        viewModel.getSaloon.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    if (page == 1) {
                        binding.swipeRefresh.isRefreshing = false
                        (activity as HomeActivity?)?.hideLoadingIndicator()
                    } else {
                        binding.bottomLoader.visibility = View.GONE
                    }
                    if (it.value.status != 0) {
                        isLoading = false
                        if (page == 1) {
                            saloonsList.clear()
                        }
                        if (it.value.data.totalPages != null) {
                            totalPage = it.value.data.totalPages.toInt()
                        }
                        for (saloon in it.value.data.items) {
                            saloonsList.add(
                                SaloonDto(
                                    saloon.id.toInt(),
                                    saloon.SaloonImages,
                                    saloon.name,
                                    if (saloon.status == "open") true else false,
                                    saloon.status,
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
                        setSaloonAdopter()
                        requireView().snackbar(it.value.message)
                    }
                }

                is Resource.Failure -> {
                    setSaloonAdopter()
                    Log.d("response", "failure " + it.toString())
                    isLoading = false
                    if (page == 1) {
                        binding.swipeRefresh.isRefreshing = false
                        (activity as HomeActivity?)?.hideLoadingIndicator()
                    } else {
                        binding.bottomLoader.visibility = View.GONE
                    }
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.getSaloons(page = page, isActive = isActive)
        if (page == 1) {
            if (!reload) {
                (activity as HomeActivity?)?.showLoadingIndicator()
            }
        } else {
            binding.bottomLoader.visibility = View.VISIBLE
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            if (Appelement.reload) {
                Appelement.reload = false
                if (FilterSelection.filterData != null) {
                    if (FilterSelection.filterData!!.status != null && FilterSelection.filterData!!.status != "") {
                        if (FilterSelection.filterData!!.status == "Active") {
                            isActive = true
                        } else {
                            isActive = false
                        }
                        getSaloons(false)
                    }


                } else {
                    getSaloons(false)
                }

            }
        }
        else{
            if (isActive!=null){
                isActive=null
                Appelement.reload = true
                FilterSelection.filterData=null
            }
        }

    }

}