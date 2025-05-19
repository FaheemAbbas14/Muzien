package com.tt.muzien.ui.views


/**
 * Created by Faheem Abbas on 27/12/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tt.muzien.R
import com.tt.muzien.data.dto.CalendarDay
import com.tt.muzien.ui.adapters.HorizontalCalenderAdapter
import java.text.SimpleDateFormat
import java.util.*

class HorizontalCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val recyclerView: RecyclerView
    private val adapter: HorizontalCalenderAdapter
    private val tvMonthYear: TextView
    private val btnPreviousMonth: TextView
    private val btnNextMonth: TextView

    private val calendar: Calendar = Calendar.getInstance()
    private var onDaySelectedListener: ((CalendarDay) -> Unit)? = null
    private var onMonthChangedListener: ((startDate: String, endDate: String) -> Unit)? = null
    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.custom_horizental_calender, this, true)

        recyclerView = findViewById(R.id.calendarRecyclerView)
        tvMonthYear = findViewById(R.id.tvMonthYear)
        btnPreviousMonth = findViewById(R.id.prevMonth)
        btnNextMonth = findViewById(R.id.nextMonth)

        adapter = HorizontalCalenderAdapter(emptyList()) { position ->
            onDaySelectedListener?.invoke(adapter.getDay(position))
        }

        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        btnPreviousMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        btnNextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }

        // Initialize with current month
        updateCalendar()
    }

    fun setOnDaySelectedListener(listener: (String) -> Unit) {
        this.onDaySelectedListener = { selectedDay ->
            val formattedDate = formatSelectedDate(
                dayOfMonth = selectedDay.dayOfMonth,
                calendar = calendar,
                format = "yyyy-MM-dd" // Change this to your desired format
            )
            listener(formattedDate)
        }
    }
    fun setOnMonthChangedListener(listener: (startDate: String, endDate: String) -> Unit) {
        this.onMonthChangedListener = listener
    }
    fun setMonthFromDate(dateString: String, format: String = "yyyy-MM-dd") {
        if (dateString!="") {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            val date = sdf.parse(dateString) ?: return

            calendar.time = date
            updateCalendar()
        }
    }
    private fun updateCalendar() {
        // Update the month-year header
        val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        tvMonthYear.text = formatter.format(calendar.time)

        // Generate days for the current month
        val days = generateDaysForMonth(calendar)
        adapter.setDays(days)
// 👇 Add this line to unselect the day when month changes
        adapter.setSelected(-1)
        recyclerView.scrollToPosition(0)
        // Disable next button if current month and year match today's month and year
        val today = Calendar.getInstance()
        val isCurrentMonth = calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)

        btnNextMonth.isEnabled = !isCurrentMonth
        btnNextMonth.alpha = if (btnNextMonth.isEnabled) 1.0f else 0.5f  // Optional: dim disabled button


        // Notify start and end date to listener
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val startCalendar = calendar.clone() as Calendar
        startCalendar.set(Calendar.DAY_OF_MONTH, 1)

        val endCalendar = calendar.clone() as Calendar
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))

        val startDate = dateFormat.format(startCalendar.time)
        val endDate = dateFormat.format(endCalendar.time)

        onMonthChangedListener?.invoke(startDate, endDate)
    }

    private fun generateDaysForMonth(calendar: Calendar): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()

        val tempCalendar = calendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1) // Move to the first day of the month

        val maxDay = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (day in 1..maxDay) {
            val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(tempCalendar.time)

            days.add(CalendarDay(dayOfWeek, day))
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return days
    }

    fun formatSelectedDate(dayOfMonth: Int, calendar: Calendar, format: String): String {
        val selectedCalendar = calendar.clone() as Calendar
        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
        return dateFormatter.format(selectedCalendar.time)
    }

    fun setDays(days: List<CalendarDay>) {
        adapter.setDays(days)
    }

    fun setCurrentDay(day: Int) {
        adapter.setSelected(day)
        onDaySelectedListener?.invoke(adapter.getDay(day))
        recyclerView.scrollToPosition(day)
    }
}
