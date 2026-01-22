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
import java.util.Date
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

        // Your header (kept exactly as you want)
        var daysOfWeek = listOf("SAT", "SUN", "MON", "TUE", "WED", "THU", "FRI")
        if (Locale.getDefault().language == "ar") {
            daysOfWeek = listOf("السبت", "الأحد", "الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة")
        }
        daysList.addAll(daysOfWeek)

        // Move calendar to 1st of month
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Get first day of month (1 = Sunday ... 7 = Saturday)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Convert Calendar day to your SAT-based index
        val firstDayIndex = when (dayOfWeek) {
            Calendar.SATURDAY -> 0
            Calendar.SUNDAY -> 1
            Calendar.MONDAY -> 2
            Calendar.TUESDAY -> 3
            Calendar.WEDNESDAY -> 4
            Calendar.THURSDAY -> 5
            Calendar.FRIDAY -> 6
            else -> 0
        }

        // Add leading blanks so 1st lands under correct weekday
        repeat(firstDayIndex) {
            daysList.add("")
        }

        // Format dates as yyyy-MM-dd
        val monthFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (day in 1..daysInMonth) {
            val dayString = String.format("%02d", day)

            val date = monthFormatter
                .format(calendar.time)
                .replace(Regex("-\\d{2}$"), "-$dayString")

            daysList.add(date)
        }

        return daysList
    }


    // === Public APIs ===

    /** Jump to an exact date (month is 0-based; Jan=0, Dec=11). */
    fun setDate(year: Int, monthZeroBased: Int, dayOfMonth: Int = 1) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthZeroBased)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateCalendar()
    }

    /** Jump using a java.util.Date. */
    fun setDate(date: Date) {
        calendar.time = date
        // Ensure the month grid reflects the chosen month correctly
        calendar.set(Calendar.DAY_OF_MONTH, maxOf(1, calendar.get(Calendar.DAY_OF_MONTH)))
        updateCalendar()
    }

    /** Jump using a string; default pattern is "yyyy-MM-dd". */
    fun setDate(dateString: String, pattern: String = "yyyy-MM-dd") {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        val parsed = runCatching { sdf.parse(dateString) }.getOrNull()
        if (parsed != null) {
            setDate(parsed)
        } else {
            // Silently ignore invalid input to avoid crashes
        }
    }

    /** Convenience: jump back to the current month (today). */
    fun goToToday() {
        calendar.time = Date()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        updateCalendar()
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
