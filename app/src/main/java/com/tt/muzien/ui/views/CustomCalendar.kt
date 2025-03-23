package com.tt.muzien.ui.views


/**
 * Created by Faheem Abbas on 26/12/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */


import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import com.tt.muzien.R
import com.tt.muzien.ui.adapters.CalendarAdapter

class CustomCalendar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    interface OnDateSelectedListener {
        fun onDateSelected(date: String)
    }

    private var dateSelectedListener: OnDateSelectedListener? = null

    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var monthYearText: TextView
    private lateinit var prevMonth: TextView
    private lateinit var nextMonth: TextView

    private val calendar = Calendar.getInstance()

    // Declare the adapter as a property
    private lateinit var calendarAdapter: CalendarAdapter

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
        calendarRecyclerView.layoutManager = GridLayoutManager(context, 7) // 7 days in a week
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

        // Initialize the adapter
        calendarAdapter = CalendarAdapter(daysInMonth) { selectedDate ->
            dateSelectedListener?.onDateSelected(selectedDate)
            calendarAdapter.updateSelectedDate(selectedDate) // Highlight the selected date
        }

        calendarRecyclerView.adapter = calendarAdapter
    }

    private fun getDaysInMonth(): List<String> {
        val daysList = mutableListOf<String>()
        val daysOfWeek = listOf("SAT", "SUN", "MON", "TUE", "WED", "THU", "FRI")
        daysList.addAll(daysOfWeek)

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 0 until firstDayOfMonth) {
            daysList.add("")
        }

        for (day in 1..daysInMonth) {
            val dayString = String.format("%02d", day) // Format day with leading zero
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(calendar.time).replace(Regex("-\\d{2}$"), "-$dayString")
            daysList.add(date)
        }

        return daysList
    }

    // Public method to set the listener
    fun setOnDateSelectedListener(listener: OnDateSelectedListener) {
        this.dateSelectedListener = listener
    }
}
