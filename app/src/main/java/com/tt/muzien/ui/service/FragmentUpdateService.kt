package com.tt.muzien.ui.service

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tt.muzien.R
import com.tt.muzien.data.dto.ServiceInfo
import com.tt.muzien.data.dto.ServiceSaloon
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.network.ServiceApi
import com.tt.muzien.data.repository.ServiceRepository
import com.tt.muzien.data.requests.AddSaloonService
import com.tt.muzien.data.requests.AddServiceSaloonRequest
import com.tt.muzien.data.responses.ServiceDetailsInfo
import com.tt.muzien.databinding.FragmentUpdateServiceBinding
import com.tt.muzien.ui.adapters.ServiceUpdateListAdopter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.saloon.tabs.FragmentAddSaloonServices
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.Appelement
import com.tt.muzien.utilities.FilterSelection


class FragmentUpdateService :
    BaseFragment<ServiceViewModel, FragmentUpdateServiceBinding, ServiceRepository>() {
    var service: ServiceInfo? = null
    var category: String? = null
    var saloonId: String? = "0"
    var isFromAdd = false
    private val salonList = arrayListOf<ServiceSaloon>()
    var adapter: ServiceUpdateListAdopter? = null
    var serviceDetailsInfo: ServiceDetailsInfo? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setSaloonRepo((activity as HomeActivity?)?.getSaloonRepo()!!)
        binding.imgBack.setOnClickListener {
            //Appelement.reload=true
            FilterSelection.filterData = null
            (activity as HomeActivity?)?.popFragment()
        }
        binding.imgEdit.setOnClickListener {
            var nextFragment = FragmentAddSaloonServices()
            nextFragment.service = service
            nextFragment.category = category
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.llSave.setOnClickListener {
            if (service?.service != null) {
                addSaloonService()
            } else {
                addSaloonService()
            }
        }
        getServicesDetails(false)
        if (service?.service != null) {
            binding.txtSave.text = resources.getString(R.string.update)
        } else {
            binding.txtSave.text = resources.getString(R.string.save)
        }
        binding.swipeRefresh.recyclerView = binding.rcySaloons
        binding.swipeRefresh.setOnRefreshListener {
            // binding.swipeRefresh.isRefreshing = false
            //page=1
            getServicesDetails(true)
        }
    }

    private fun setData() {
        val iconRequestListener = object : RequestListener<Drawable> {

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean,
            ): Boolean {
                Log.d("imageLoaded", "success ${serviceDetailsInfo?.name}")

                //  holder.imgProfilePic.scaleType = ImageView.ScaleType.CENTER_CROP
                return false
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean,
            ): Boolean {
//                holder.imgProfilePic.scaleType = ImageView.ScaleType.CENTER_INSIDE
                Log.d("imageLoaded", "failed ${serviceDetailsInfo?.name}")
                return false
            }


        }
        Glide.with(binding.imgCover)
            .load(serviceDetailsInfo?.image)
            .listener(iconRequestListener)
            .placeholder(R.drawable.hair_cut)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // Cache both original & transformed image
            .skipMemoryCache(false)  // Cache in memory
            .into(binding.imgCover)
        binding.txtItemName.text = serviceDetailsInfo?.name
        binding.txtItemCategory.text = "Category: $category"
        binding.txtItemDuration.text =
            "${serviceDetailsInfo?.duration} mins | SAR ${serviceDetailsInfo!!.price / 100}"
        setSaloonsAdapter()
    }

    private fun setSaloonsAdapter() {

        adapter = ServiceUpdateListAdopter(salonList, requireContext()) { position, isEnabled ->
            binding.llSave.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_blue_100))
            binding.txtSave.setTextColor(resources.getColor(R.color.white))


        }

        binding.rcySaloons.layoutManager = LinearLayoutManager(requireActivity())
        binding.rcySaloons.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(
            binding.rcySaloons.context,
            (binding.rcySaloons.layoutManager as LinearLayoutManager).orientation
        )

        binding.rcySaloons.addItemDecoration(dividerItemDecoration)
    }

    override fun getViewModel(): Class<ServiceViewModel> {
        return ServiceViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentUpdateServiceBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        ServiceRepository(remoteDataSource.buildApi(ServiceApi::class.java, requireContext()))

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, false)
        (activity as HomeActivity?)?.setSystemWindow(false)
        (activity as HomeActivity?)?.changeStatusBarColor(Color.TRANSPARENT)
        (activity as HomeActivity?)?.hideTabs()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, false)
            (activity as HomeActivity?)?.setSystemWindow(false)
            (activity as HomeActivity?)?.changeStatusBarColor(Color.TRANSPARENT)
            (activity as HomeActivity?)?.hideTabs()
            if (Appelement.reload) {
                Appelement.reload = false
                getServicesDetails(false)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
        (activity as HomeActivity?)?.setSystemWindow(true)
        (activity as HomeActivity?)?.changeStatusBarColor(Color.WHITE)
        (activity as HomeActivity?)?.showTabs()
    }

    private fun getServicesDetails(reload: Boolean) {
        viewModel.getServicesDetails.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    if (it.value.status == 1) {
                        serviceDetailsInfo = it.value.data
                        //  if (service?.service == null) {
                        getSaloons()
//                        } else {
//                            (activity as HomeActivity?)?.hideLoadingIndicator()
//                            setData()
//                        }

                    }
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())
                    binding.swipeRefresh.isRefreshing = false
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.getServicesDetails(service?.id.toString())
        if (!reload) {
            (activity as HomeActivity?)?.showLoadingIndicator()
        }
    }

    private fun getSaloons() {
        viewModel.getServicesStatus.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    binding.swipeRefresh.isRefreshing = false
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        salonList.clear()
                        for (saloon in it.value.data) {
                            if (saloonId != "0") {
                                if (saloon.id.toString() == saloonId) {
                                    salonList.add(
                                        ServiceSaloon(
                                            saloon.id.toString(),
                                            service?.id.toString(),
                                            saloon.name,
                                            saloon.address,
                                            if (saloon.duration > 0) saloon.duration.toString() else serviceDetailsInfo?.duration.toString(),
                                            "SAR",
                                            if (saloon.price > 0) (saloon.price / 100).toString() else (serviceDetailsInfo?.price!! / 100).toString(),
                                            saloon.serviceEnabled
                                        )
                                    )
                                }
                            } else {
                                salonList.add(
                                    ServiceSaloon(
                                        saloon.id.toString(),
                                        service?.id.toString(),
                                        saloon.name,
                                        saloon.address,
                                        if (saloon.duration > 0) saloon.duration.toString() else serviceDetailsInfo?.duration.toString(),
                                        "SAR",
                                        if (saloon.price > 0) (saloon.price / 100).toString() else (serviceDetailsInfo?.price!! / 100).toString(),
                                        saloon.serviceEnabled
                                    )
                                )
                            }

                        }
                        setData()
                    } else {
                        requireView().snackbar(it.value.message)
                    }
                }

                is Resource.Failure -> {
                    Log.d("response", "failure " + it.toString())
                    binding.swipeRefresh.isRefreshing = false
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    handleApiError(it)
                }

                else -> {}
            }
        }
        viewModel.getServicesStatus(service?.id.toString())
    }

    private fun addSaloonService() {
        viewModel.addSaloonService.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        Appelement.reload = true
                        if (service != null) {
                            if (isFromAdd) {
                                (activity as HomeActivity?)?.popFragment()
                            }
                            (activity as HomeActivity?)?.popFragment()
                            requireView().snackbar("Service updated successfully")
                        } else {
                            (activity as HomeActivity?)?.popFragment()
                            (activity as HomeActivity?)?.popFragment()
                            (activity as HomeActivity?)?.popFragment()
                            requireView().snackbar("Service enabled successfully")
                        }

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
        var saloonData = arrayListOf<AddSaloonService>()
        for (saloon in adapter!!.getData()) {
            saloonData.add(
                AddSaloonService(
                    saloon.saloonId,
                    saloon.serviceId,
                    saloon.isEnabled.toString(),
                    saloon.duration.toInt(),
                    saloon.price.toInt() * 100
                )
            )
        }
        viewModel.addSaloonService(
            AddServiceSaloonRequest(saloonData)
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
}