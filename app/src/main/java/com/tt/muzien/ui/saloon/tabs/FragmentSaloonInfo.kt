package com.tt.muzien.ui.saloon.tabs

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
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
import com.tt.muzien.ui.adapters.WorkingHoursAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.zabihah.ui.ui.interfaces.OnItemClickListner


class FragmentSaloonInfo : BaseFragment<HomeViewModel, FragmentSaloonInfoBinding, HomeRepository>(),
    OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private val workingHourrList = arrayListOf<WorkingHourData>()
    private var workingHoursAdopter: WorkingHoursAdapter? = null
    private var isExpanded = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAboutData()
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

    private fun setAboutData() {
        val fullText =
            "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type"

        // Truncated text preview (show first 100 chars)
        val previewText = fullText.substring(0, 100) + "..." // Truncated preview text

        // Set initial text with "Read More" link
        setTextWithToggle(previewText, fullText)
    }

    private fun setTextWithToggle(previewText: String, fullText: String) {
        val spannable = SpannableString(previewText + " Read More")

        // Set click listener on "Read More" text
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                toggleText(fullText, previewText)
            }
        }, previewText.length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.txtAbout.text = spannable
        binding.txtAbout.movementMethod =
            LinkMovementMethod.getInstance() // Enable clicking the text
    }

    private fun toggleText(fullText: String, previewText: String) {
        try {

            val spannable: SpannableString
            if (isExpanded) {
                // Show preview text with "Read More"
                spannable = SpannableString(previewText + " Read More")
                // Set click listener for "Read More"
                spannable.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        toggleText(fullText, previewText)
                    }
                }, previewText.length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                // Show full text with "Read Less"
                spannable = SpannableString(fullText + " Read Less")
                // Set click listener for "Read Less"
                spannable.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        toggleText(fullText, previewText)
                    }
                }, fullText.length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            binding.txtAbout.text = spannable
            binding.txtAbout.movementMethod =
                LinkMovementMethod.getInstance() // Enable clicking the text

            isExpanded = !isExpanded // Toggle state between expanded and collapsed

        } catch (e: Exception) {
            e.printStackTrace()
        }
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
        workingHoursAdopter = WorkingHoursAdapter(
            workingHourrList,
            requireContext(),
            clickListener
        )
        binding.rcyWorkingHours.adapter = workingHoursAdopter


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