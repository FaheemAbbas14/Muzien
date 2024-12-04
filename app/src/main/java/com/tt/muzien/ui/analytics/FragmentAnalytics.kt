package com.tt.muzien.ui.analytics

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.tt.muzien.R
import com.tt.muzien.data.dto.PersonDto
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.data.repository.HomeRepository
import com.tt.muzien.databinding.FragmentAnalyticsBinding
import com.tt.muzien.ui.adopters.PerformerListAdopter
import com.tt.muzien.ui.base.BaseFragment
import com.tt.muzien.ui.home.FragmentFilter
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.home.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.random.Random


class FragmentAnalytics : BaseFragment<HomeViewModel, FragmentAnalyticsBinding, HomeRepository>() {
    private var lineChart: LineChart? = null
    private val topPerformerList = arrayListOf<PersonDto>()
    private var isRevenueFilter = false
    private var revenueDuration: String = ""
    private var revenueFromDate: String = ""
    private var revenueToDate: String = ""
    private var bookingDuration: String = ""
    private var bookingFromDate: String = ""
    private var bookingToDate: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lineChart = binding.lineChart
        setdata()

        binding.imgBookingFilter.setOnClickListener {
            isRevenueFilter = false
            var nextFragment = FragmentFilter()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        binding.imgRevenueFilter.setOnClickListener {
            isRevenueFilter = true
            var nextFragment = FragmentFilter()
            (activity as HomeActivity?)?.loadFragment(nextFragment)
        }
        setFragmentResultListener("requestKey") { key, bundle ->
            val selection = bundle.getString("selection")
            val fromDate = bundle.getString("fromDate")
            val toDate = bundle.getString("toDate")
            if (selection != "") {
                if (!isRevenueFilter) {
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
                setdata()
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setdata() {
        setPerformerAdopter()
        setGraph()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDatesInRange(
        startDate: String,
        endDate: String,
        dateFormat: String = "d/M/yyyy"
    ): ArrayList<String> {
        val formatter = DateTimeFormatter.ofPattern(dateFormat, Locale.getDefault())
        val start = LocalDate.parse(startDate, formatter)
        val end = LocalDate.parse(endDate, formatter)
        val desiredFormatter = DateTimeFormatter.ofPattern("d/M", Locale.getDefault())
        val dates = arrayListOf<String>()
        var currentDate = start
        while (!currentDate.isAfter(end)) {
            dates.add(currentDate.format(desiredFormatter))
            currentDate = currentDate.plusDays(1)
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
            labels = getDatesInRange(revenueFromDate, revenueToDate)
        }

        val customValues = generateRandomFloatList(10f, 59f, labels.size)

        // Generate entries with custom values and sine wave
        val entries = mutableListOf<Entry>()
        for (i in labels.indices) {
            val x = i.toFloat()
            val sineWaveValue =
                kotlin.math.sin(i * Math.PI / 6) * 5f // Modify the amplitude as needed
            val y = customValues[i] + sineWaveValue // Add sine wave value to the custom value
            entries.add(Entry(x, y.toFloat()))
        }

        // Create dataset
        val dataSet = LineDataSet(entries, "").apply {
            color = resources.getColor(R.color.colorPrimary)
            valueTextColor = Color.BLACK
            lineWidth = 2f
            setDrawCircles(false)
            setDrawFilled(true)
            // Apply gradient fill
            val gradient = LinearGradient(
                0f, 0f, 0f, 100f,
                Color.parseColor("#00BCD4"), Color.parseColor("#4CAF50"),
                Shader.TileMode.MIRROR
            )
            fillDrawable = resources.getDrawable(R.drawable.gradient_fill)
        }

        // Set the data to the chart
        lineChart?.data = LineData(dataSet)
        // Hide the legend (color indicator)
        lineChart?.legend?.isEnabled = false
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
            axisMaximum = 60f // Adjust depending on your data range
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}k" // Add currency or units
                }
            }
        }

        // Customize Y-axis to be positive only
        lineChart?.axisLeft?.axisMinimum = 1f
        lineChart?.axisRight?.isEnabled = false

        // Chart appearance
        lineChart?.description?.isEnabled = false
        lineChart?.animateX(1500)
    }

    private fun setPerformerAdopter() {
        for (i in 0..10) {
            println("Index: $i")
            topPerformerList.add(
                PersonDto(
                    "https://graph.facebook.com/580534664534485/picture?type=large",
                    "Name $i",
                    "Profession $i",
                    "${10 * i} Bookings",
                    "SAR ${5 * i}"
                )
            )
        }
        binding.rcyTopPerformer.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcyTopPerformer.adapter =
            PerformerListAdopter(
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
        HomeRepository(remoteDataSource.buildApi(HomeApi::class.java), userPreferences)


}