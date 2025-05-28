package com.tt.muzien.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tt.muzien.R

class CalendarAdapter(
    private val days: List<String>,
    private val isMultiSelect: Boolean,
    private val onDateClick: (ArrayList<String>) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    private val selectedDates = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day, parent, false) as TextView
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.bind(day, selectedDates.contains(day))
    }

    override fun getItemCount(): Int = days.size

    fun updateSelectedDate(date: String) {
        selectedDates.clear()
        selectedDates.add(date)
        notifyDataSetChanged()
    }

    fun toggleSelectedDate(date: String) {
        if (selectedDates.contains(date)) {
            selectedDates.remove(date)
        } else {
            selectedDates.add(date)
        }
        notifyDataSetChanged()
    }

    fun getSelectedDates(): ArrayList<String> = selectedDates

    fun clearSelections() {
        selectedDates.clear()
        notifyDataSetChanged()
    }

    inner class DayViewHolder(private val dayText: TextView) : RecyclerView.ViewHolder(dayText) {
        fun bind(date: String, isSelected: Boolean) {
            dayText.text = if (date.isNotEmpty()) date.substringAfterLast("-") else ""
            dayText.setBackgroundResource(
                if (isSelected) R.drawable.circular_blue else android.R.color.transparent
            )
            dayText.setTextColor(if (isSelected) Color.WHITE else Color.BLACK)

            dayText.setOnClickListener {
                if (date.isNotEmpty()) {
                    if (isMultiSelect) {
                        toggleSelectedDate(date)
                    } else {
                        updateSelectedDate(date)
                    }
                    onDateClick(getSelectedDates())
                }
            }
        }
    }
}
