package com.tt.muzien.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.tt.muzien.R
import com.tt.muzien.data.dto.CalendarDay
import com.tt.muzien.utilities.Helper

/**
 * Created by Faheem Abbas on 27/12/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class HorizontalCalenderAdapter(
    private var days: List<CalendarDay>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<HorizontalCalenderAdapter.CalendarViewHolder>() {

    private var selectedPosition = -1

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayBackground: View = itemView.findViewById(R.id.dayBackground)
        val dayOfWeek: TextView = itemView.findViewById(R.id.dayOfWeek)
        val dayOfMonth: TextView = itemView.findViewById(R.id.dayOfMonth)
        val eventIndicatorContainer: LinearLayout = itemView.findViewById(R.id.eventIndicatorContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
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
// Add multiple dots for events
        if (day.eventDotColors.isNotEmpty()) {
            holder.eventIndicatorContainer.visibility = View.VISIBLE
            holder.eventIndicatorContainer.removeAllViews() // Clear any existing dots

            day.eventDotColors.forEach { color ->
                val dot = View(holder.itemView.context).apply {
                    layoutParams = LinearLayout.LayoutParams(Helper.dpToPx(holder.itemView.context,8), Helper.dpToPx(holder.itemView.context,8)).apply {
                        marginEnd = Helper.dpToPx(holder.itemView.context,4)
                    }
                    background = holder.itemView.context.getDrawable(R.drawable.event_dot_shape)
                    background?.setTint(color) // Set the dot color dynamically
                }
                holder.eventIndicatorContainer.addView(dot)
            }
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
    // Extension function for converting dp to px
    fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
    override fun getItemCount(): Int = days.size

    fun setDays(newDays: List<CalendarDay>) {
        days = newDays
        notifyDataSetChanged()
    }

    fun getDay(position: Int): CalendarDay = days[position]

}
