package com.tt.muzien.ui.adopters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tt.muzien.R
import com.tt.muzien.data.dto.CalendarDay

/**
 * Created by Faheem Abbas on 27/12/2024.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class HorizontalCalenderAdopter(
    private var days: List<CalendarDay>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<HorizontalCalenderAdopter.CalendarViewHolder>() {

    private var selectedPosition = -1

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayBackground: View = itemView.findViewById(R.id.dayBackground)
        val dayOfWeek: TextView = itemView.findViewById(R.id.dayOfWeek)
        val dayOfMonth: TextView = itemView.findViewById(R.id.dayOfMonth)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val day = days[position]
        holder.dayOfWeek.text = day.dayOfWeek
        holder.dayOfMonth.text = day.dayOfMonth.toString()

        // Handle selected state
        if (position == selectedPosition) {
            holder.dayBackground.isSelected = true
            holder.dayOfWeek.setTextColor(holder.itemView.context.getColor(R.color.white))
            holder.dayOfMonth.setTextColor(holder.itemView.context.getColor(R.color.white))
        } else {
            holder.dayBackground.isSelected = false
            holder.dayOfWeek.setTextColor(holder.itemView.context.getColor(R.color.colorPrimary))
            holder.dayOfMonth.setTextColor(holder.itemView.context.getColor(R.color.colorPrimary))
        }

        // Handle click
        holder.itemView.setOnClickListener {
            val oldPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPosition)
            notifyItemChanged(position)
            onClick(position)
        }
    }

    override fun getItemCount(): Int = days.size

    fun setDays(newDays: List<CalendarDay>) {
        days = newDays
        notifyDataSetChanged()
    }

    fun getDay(position: Int): CalendarDay = days[position]
}
