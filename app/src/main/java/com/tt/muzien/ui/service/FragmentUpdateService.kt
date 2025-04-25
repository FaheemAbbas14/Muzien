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
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentUpdateServiceBinding
import com.tt.muzien.ui.adapters.ServiceUpdateListAdopter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.utilities.FilterSelection


class FragmentUpdateService :
    BaseFragment<HomeViewModel, FragmentUpdateServiceBinding, HomeRepository>() {
    var service: ServiceInfo? = null
    var category: String? = null
    private val salonList = arrayListOf<ServiceSaloon>()
    var adapter: ServiceUpdateListAdopter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgBack.setOnClickListener {
            FilterSelection.filterData = null
            (activity as HomeActivity?)?.popFragment()
        }
        setData()

    }

    private fun setData() {
        val iconRequestListener = object : RequestListener<Drawable> {

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                Log.d("imageLoaded", "success ${service?.name}")

                //  holder.imgProfilePic.scaleType = ImageView.ScaleType.CENTER_CROP
                return false
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
//                holder.imgProfilePic.scaleType = ImageView.ScaleType.CENTER_INSIDE
                Log.d("imageLoaded", "failed ${service?.name}")
                return false
            }


        }
        Glide.with(binding.imgCover)
            .load(service?.icon)
            .listener(iconRequestListener)
            .placeholder(R.drawable.hair_cut)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // Cache both original & transformed image
            .skipMemoryCache(false)  // Cache in memory
            .into(binding.imgCover)
        binding.txtItemName.text = service?.name
        binding.txtItemCategory.text = "Category: $category"
        binding.txtItemDuration.text = "${service?.duration}  |  ${service?.rate}"
        setSaloonsAdapter()
    }

    private fun setSaloonsAdapter() {
        salonList.add(
            ServiceSaloon(
                "Salon A",
                "Rd. 2121 Alemal Dist. 12643 Riyadh SA",
                "45 mins",
                "SAR",
                "10",
                false
            )
        )
        salonList.add(
            ServiceSaloon(
                "Salon B",
                "Rd. 2121 Alemal Dist. 12643 Riyadh SA",
                "45 mins",
                "SAR",
                "10",
                false
            )
        )
        salonList.add(
            ServiceSaloon(
                "Salon C",
                "Rd. 2121 Alemal Dist. 12643 Riyadh SA",
                "45 mins",
                "SAR",
                "10",
                false
            )
        )

        adapter = ServiceUpdateListAdopter(salonList) { position, isEnabled ->
            salonList[position].isEnabled = isEnabled
            adapter?.notifyDataSetChanged()

        }

        binding.rcySaloons.layoutManager = LinearLayoutManager(requireActivity())
        binding.rcySaloons.adapter = adapter
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentUpdateServiceBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java,requireContext()), userPreferences)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, false)
        (activity as HomeActivity?)?.setSystemWindow(false)
        (activity as HomeActivity?)?.changeStatusBarColor(Color.TRANSPARENT)
        (activity as HomeActivity?)?.hideTabs()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        (activity as HomeActivity?)?.setStatusBarIconColor(requireActivity().window, true)
        (activity as HomeActivity?)?.setSystemWindow(true)
        (activity as HomeActivity?)?.changeStatusBarColor(Color.WHITE)
        (activity as HomeActivity?)?.showTabs()
    }
}