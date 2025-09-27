package com.tt.muzien.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tt.muzien.R
import com.tt.muzien.ui.adapters.CalendarAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class CustomCalendar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    interface OnDateSelectedListener {
        fun onDatesSelected(dates: ArrayList<String>)
    }

    private var dateSelectedListener: OnDateSelectedListener? = null

    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var monthYearText: TextView
    private lateinit var prevMonth: TextView
    private lateinit var nextMonth: TextView

    private val calendar = Calendar.getInstance()
    private lateinit var calendarAdapter: CalendarAdapter

    private var isMultiSelect: Boolean = false

    init {
        inflate(context, R.layout.custom_calendar, this)
        initViews()
        setupCalendar()
    }

    private fun initViews() {
        monthYearText = findViewById(R.id.monthYearText)
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)
        prevMonth = findViewById(R.id.prevMonth)
        nextMonth = findViewById(R.id.nextMonth)
    }

    private fun setupCalendar() {
        calendarRecyclerView.layoutManager = GridLayoutManager(context, 7)
        updateCalendar()

        prevMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        nextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }
    }

    private fun updateCalendar() {
        val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthYearText.text = formatter.format(calendar.time)

        val daysInMonth = getDaysInMonth()

        calendarAdapter = CalendarAdapter(daysInMonth, isMultiSelect) { selectedDates ->
            dateSelectedListener?.onDatesSelected(selectedDates)
        }

        calendarRecyclerView.adapter = calendarAdapter
    }

    private fun getDaysInMonth(): List<String> {
        val daysList = mutableListOf<String>()
        var daysOfWeek = listOf("SAT", "SUN", "MON", "TUE", "WED", "THU", "FRI")
        if (Locale.getDefault().language == "ar") {
            daysOfWeek =
                listOf("السبت", "الأحد", "الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة")
        }
        daysList.addAll(daysOfWeek)

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 0 until firstDayOfMonth) {
            daysList.add("")
        }

        for (day in 1..daysInMonth) {
            val dayString = String.format("%02d", day)
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(calendar.time).replace(Regex("-\\d{2}$"), "-$dayString")
            daysList.add(date)
        }

        return daysList
    }

    fun setOnDateSelectedListener(listener: OnDateSelectedListener) {
        this.dateSelectedListener = listener
    }

    fun setMultiSelectEnabled(enabled: Boolean) {
        isMultiSelect = enabled
        updateCalendar()
    }

    fun getSelectedDates(): List<String> {
        return if (::calendarAdapter.isInitialized) calendarAdapter.getSelectedDates() else emptyList()
    }

    fun clearSelection() {
        if (::calendarAdapter.isInitialized) calendarAdapter.clearSelections()
    }
}
