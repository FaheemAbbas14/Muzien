package com.tt.muzien.ui.saloon.tabs

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.tt.muzien.R
import com.tt.muzien.data.dto.PersonDto
import com.tt.muzien.data.dto.SaloonDto
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.network.Resource
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentAnalyticsBinding
import com.tt.muzien.ui.adapters.PerformerListAdapter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.handleApiError
import com.tt.muzien.ui.home.FragmentFilter
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import com.tt.muzien.ui.snackbar
import com.tt.muzien.utilities.FilterSelection
import com.tt.muzien.utilities.TimeHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.random.Random
import kotlin.toString

class FragmentSaloonAnalytics :
    BaseFragment<HomeViewModel, FragmentAnalyticsBinding, HomeRepository>() {
    private var lineChart: LineChart? = null
    private val topPerformerList = arrayListOf<PersonDto>()
    private var isRevenueFilter = false
    private var revenueDuration: String? = null
    private var revenueFromDate: String? = null
    private var revenueToDate: String? = null
    private var bookingDuration: String? = null
    private var bookingFromDate: String? = null
    private var bookingToDate: String? = null
    var selectedSaloon: SaloonDto? = null
    var scheduleBookings = 0
    var overdueBookings = 0
    var cancledBookings = 0
    var completedBookings = 0
    var totalEarnings = 0
    private val graphMap = HashMap<String, Int>()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // binding.homeLayout.setBackgroundColor(Color.argb(10, 30, 69, 148))
        lineChart = binding.lineChart
        if (bookingFromDate == null) {
            val dates = TimeHelper.getWeekAndMonthDates()
            bookingFromDate = dates["startOfWeek"]
            bookingToDate = dates["endOfWeek"]
            revenueFromDate = dates["startOfMonth"]
            revenueToDate = dates["endOfMonth"]
            getAnalytics()
        }


        binding.imgBookingFilter.setOnClickListener {
            isRevenueFilter = false
            var nextFragment = FragmentFilter()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.imgRevenueFilter.setOnClickListener {
            isRevenueFilter = true
            var nextFragment = FragmentFilter()
            nextFragment.isFromRevenue = true
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        if (FilterSelection.filterData != null) {
            val selection = FilterSelection.filterData!!.selection
            val fromDate = FilterSelection.filterData!!.from
            val toDate = FilterSelection.filterData!!.to
            if (selection != "") {
                if (!FilterSelection.filterData!!.fromRevenue) {
                    bookingDuration = selection.toString()
                    bookingFromDate = fromDate.toString()
                    bookingToDate = toDate.toString()
                    if (selection == "Custom") {
                        binding.txtDuration.text = "$fromDate To ${toDate}"
                    } else {
                        binding.txtDuration.text = selection
                    }
                } else {
                    revenueDuration = selection.toString()
                    revenueFromDate = fromDate.toString()
                    revenueToDate = toDate.toString()
                    if (selection == "Custom") {
                        binding.txtRevenueType.text = "$fromDate To ${toDate}"
                    } else {
                        binding.txtRevenueType.text = selection
                    }
                }
                getAnalytics()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setdata() {
        binding.txtScheduled.text = "$scheduleBookings"
        binding.txtOverdue.text = "$overdueBookings"
        binding.txtCompleted.text = "$completedBookings"
        binding.txtCancelled.text = "$cancledBookings"
        binding.txtEarningAmount.text = "$totalEarnings"
        setPerformerAdopter()
        setGraph()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDatesInRange(
        startDate: String,
        endDate: String,
        dateFormat: String = "yyyy-MM-dd"
    ): ArrayList<String> {
        val dates = arrayListOf<String>()
        try {
            val formatter = DateTimeFormatter.ofPattern(dateFormat, Locale.getDefault())
            val start = LocalDate.parse(startDate, formatter)
            val end = LocalDate.parse(endDate, formatter)
            val desiredFormatter = DateTimeFormatter.ofPattern("d/M", Locale.getDefault())
            var currentDate = start
            while (!currentDate.isAfter(end)) {
                dates.add(currentDate.format(desiredFormatter))
                currentDate = currentDate.plusDays(1)
            }

            return dates

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dates
    }

    fun generateRandomFloatList(min: Float, max: Float, count: Int): List<Float> {
        return List(count) { Random.nextFloat() * (max - min) + min }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setGraph() {
        var labels = arrayListOf<String>(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )

        if (revenueDuration == "Week") {
            labels.clear()
            labels.addAll(
                listOf(
                    "Mon", "Tue", "Wed", "Thr", "Fri", "Sat", "Sun"
                )
            )
        } else if (revenueDuration == "Custom") {
            labels.clear()
            revenueFromDate?.let { labels = getDatesInRange(it, revenueToDate!!) }
        }

        // val customValues = generateRandomFloatList(10f, 55f, labels.size)

        // Generate entries with custom values and sine wave
        val entries = mutableListOf<Entry>()
        var maxValue = 0
        for (i in labels.indices) {
            val x = i.toFloat()
            val value = x.toString()
            var data = graphMap.get(value)
            if (data != null) {
                if (data > maxValue) {
                    maxValue = data
                }
            }
            if (data == null) {
                data = 0
            }
            val sineWaveValue =
                kotlin.math.sin(i * Math.PI / 6) * 5f // Modify the amplitude as needed
            var y: Double = data.toDouble()

            if (data > 0) {
                y = data + sineWaveValue // Add sine wave value to the custom value

            }
            Log.d("GraphValues", "label $x value $y data $data value $value")
            entries.add(Entry(x, y.toFloat()))
        }

        // Create dataset
        val dataSet = LineDataSet(entries, "").apply {
            color = Color.parseColor("#001DFF")
            valueTextColor = Color.BLACK
            lineWidth = 1f
            setDrawCircles(false)
            setDrawFilled(true)
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            // Apply gradient drawable as fill
            val gradientDrawable: Drawable? =
                ContextCompat.getDrawable(requireContext(), R.drawable.gradient_fill)
            fillDrawable = gradientDrawable
        }

        // Set the data to the chart
        lineChart?.data = LineData(dataSet)
        // Hide the legend (color indicator)
        lineChart?.legend?.isEnabled = false
        Log.d("GraphData", "max value $maxValue")
        // Customize X-Axis to show month names
        lineChart?.xAxis?.apply {
            granularity = 1f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return labels.getOrNull(value.toInt()) ?: ""
                }
            }
            position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        }
        // Customize Y-axis to show labels, set range and formatting
        lineChart?.axisLeft?.apply {
            axisMinimum = 1f
            axisMaximum = (maxValue + 200).toFloat()// Adjust depending on your data range
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}" // Add currency or units
                }
            }
        }

        // Remove horizontal grid lines
        lineChart?.axisLeft?.setDrawGridLines(false)
        lineChart?.axisRight?.setDrawGridLines(false)
        // Customize Y-axis to be positive only
        lineChart?.axisLeft?.axisMinimum = 1f
        lineChart?.axisRight?.isEnabled = false

        // Chart appearance
        lineChart?.description?.isEnabled = false
        // lineChart?.animateX(1500)
        // Disable pinch zoom (zooming with two fingers)
        lineChart?.setPinchZoom(false)

// Disable scaling on X and Y axes
        lineChart?.isScaleXEnabled = false
        lineChart?.isScaleYEnabled = false

// Optional: Disable double-tap zoom
        lineChart?.isDoubleTapToZoomEnabled = false
        lineChart?.invalidate()
    }


    private fun setPerformerAdopter() {
        binding.rcyTopPerformer.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcyTopPerformer.adapter =
            PerformerListAdapter(
                topPerformerList,
                requireContext(),
            )
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAnalyticsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        HomeRepository(
            remoteDataSource.buildApi(HomeApi::class.java, requireContext()),
            userPreferences
        )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAnalytics() {
        viewModel.getAnalytics.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        topPerformerList.clear()
                        for (performer in it.value.data.topPerformers) {

                            topPerformerList.add(
                                PersonDto(
                                    performer.picture,
                                    performer.fullName ?: "",
                                    "Hair Stylist",
                                    "${performer.totalBookings} Bookings",
                                    "SAR ${5 * 1}"
                                )
                            )

                        }
                        for (analytics in it.value.data.booking) {
                            if (analytics.status == "completed") {
                                completedBookings = analytics.total_count.toInt()
                            } else if (analytics.status == "cancelled") {
                                cancledBookings = analytics.total_count.toInt()
                            } else if (analytics.status == "overdue") {
                                overdueBookings = analytics.total_count.toInt()
                            } else if (analytics.status == "scheduled") {
                                scheduleBookings = analytics.total_count.toInt()
                            }
                        }
                        totalEarnings = it.value.data.totalEarning.toInt()
                        if (FilterSelection.filterData != null) {
                            val selection = FilterSelection.filterData!!.selection
                            if (selection == "Month") {
                                getMonthlyRevenue()
                            } else {
                                getWeeklyRevenue()
                            }
                        } else {
                            getMonthlyRevenue()
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
        viewModel.getAnalytics(
            saloonIds = selectedSaloon?.id.toString(),
            startDate = bookingFromDate,
            endDate = bookingToDate
        )
        (activity as HomeActivity?)?.showLoadingIndicator()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeeklyRevenue() {
        viewModel.getWeeklyRevenue.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        graphMap.clear()
                        for (revenue in it.value.data?.revenue!!) {
                            var key=revenue.date?.split("-")[2]!!
                            graphMap.put("${key.toFloat()}", revenue.count.toInt())
                        }
                        setdata()
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
        viewModel.getWeeklyRevenue(
            startDate = revenueFromDate,
            endDate = revenueToDate
        )
        // (activity as HomeActivity?)?.showLoadingIndicator()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMonthlyRevenue() {
        viewModel.getMonthlyRevenue.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d("response", "success " + it.toString())
                    (activity as HomeActivity?)?.hideLoadingIndicator()
                    if (it.value.status != 0) {
                        graphMap.clear()
                        for (revenue in it.value.data?.revenue!!) {
                            graphMap.put("${revenue.month?.toFloat()}", revenue.count.toInt())
                        }
                        setdata()
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
        viewModel.getMonthlyRevenue(
        )
        // (activity as HomeActivity?)?.showLoadingIndicator()
    }
}