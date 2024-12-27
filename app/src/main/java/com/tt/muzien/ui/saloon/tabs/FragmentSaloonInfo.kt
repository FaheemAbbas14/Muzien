package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tt.muzien.R
import com.tt.muzien.data.dto.WorkingHourData
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentSaloonInfoBinding
import com.tt.muzien.ui.adopters.WorkingHoursAdopter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentSaloonInfo : BaseFragment<HomeViewModel, FragmentSaloonInfoBinding, HomeRepository>(),
    OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private val workingHourrList = arrayListOf<WorkingHourData>()
    private var workingHoursAdopter: WorkingHoursAdopter? = null

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.imgAddHoliday.setOnClickListener {
            var nextFragment = FragmentAddHoliday()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.imgAddWorkingHour.setOnClickListener {
            var nextFragment = FragmentAddWorkingDay()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        // Initialize the SupportMapFragment and request the map.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setData()
    }

    private fun setData() {
        setWorkingHourAdopter()
    }

    private fun setWorkingHourAdopter() {
        workingHourrList.add(WorkingHourData("1", "Saturday - Thursday", "10:00 AM - 11:00 PM"))
        workingHourrList.add(WorkingHourData("2", "Friday", "02:00 AM - 11:00 PM"))
        val clickListener = object : OnItemClickListner {
            override fun onItemClick(position: Int) {
                workingHourrList.removeAt(position)
                workingHoursAdopter?.notifyDataSetChanged()
            }
        }
        binding.rcyWorkingHours.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        workingHoursAdopter = WorkingHoursAdopter(
            workingHourrList,
            requireContext(),
            clickListener
        )
        binding.rcyWorkingHours.adapter =workingHoursAdopter


    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSaloonInfoBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java), userPreferences)

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Add a marker at a specific location and move the camera
        val location = LatLng(-34.0, 151.0) // Example coordinates (Sydney, Australia)
        map.addMarker(MarkerOptions().position(location).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
    }

}