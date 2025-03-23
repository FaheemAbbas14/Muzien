package com.tt.muzien.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tt.muzien.R

/**
 * Created by Faheem Abbas on 26/12/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class CalendarAdapter(
    private val days: List<String>,
    private val onDateClick: (String) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    private var selectedDate: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day, parent, false)
        return DayViewHolder(view, onDateClick)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(days[position], days[position] == selectedDate)
    }

    override fun getItemCount(): Int = days.size

    fun updateSelectedDate(date: String) {
        selectedDate = date
        notifyDataSetChanged() // Refresh the calendar to highlight the selected date
    }

    class DayViewHolder(itemView: View, private val onDateClick: (String) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val dayText: TextView = itemView as TextView

        fun bind(day: String, isSelected: Boolean) {
            dayText.text = if (day.isNotEmpty()) day.substringAfterLast("-") else ""

            // Apply styles for selected and non-selected dates
            if (isSelected) {
                dayText.setBackgroundResource(R.drawable.circular_blue)
                dayText.setTextColor(Color.WHITE)
            }

            // Handle click events
            dayText.setOnClickListener {
                if (day.isNotEmpty()) {
                    onDateClick(day)
                }
            }
        }
    }
}
