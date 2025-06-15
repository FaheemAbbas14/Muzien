package com.tt.muzien.ui.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tt.muzien.R
import com.tt.muzien.data.dto.SubscriptionData
import com.zabihah.ui.ui.interfaces.OnItemClickListner
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


/**
 * Created by Faheem Abbas on 24/01/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class SubscriptionListAdapter(
    private val itemList: List<SubscriptionData>,
    private val context: Context,
    private val listener: OnItemClickListner,
) :
    RecyclerView.Adapter<SubscriptionListAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val mainLayout: ConstraintLayout = itemView.findViewById(R.id.mainLayout)
        val txtReneiw: TextView = itemView.findViewById(R.id.textView17)
        val txtStartDate: TextView = itemView.findViewById(R.id.textView19)
        val txtEndDate: TextView = itemView.findViewById(R.id.textView21)
        val txtOverdue: TextView = itemView.findViewById(R.id.textView22)
        val txtDays: TextView = itemView.findViewById(R.id.textView23)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.saloon_subscription_item, parent, false)

        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var item: SubscriptionData = itemList[position]

        holder.txtName.text = item.name
        holder.txtStartDate.text = item.startDate
        holder.txtEndDate.text = item.endData
        var days = getDaysBetweenDates(item.startDate, item.endData, "yyyy-MM-dd")
        if (item.isExpired) {
            holder.txtDays.setTextColor(context.getColor(R.color.red_text))
            holder.txtOverdue.setTextColor(context.getColor(R.color.red_text))
            holder.txtReneiw.visibility = View.VISIBLE
            holder.mainLayout.setBackgroundColor(context.getColor(R.color.overduebg))
        }
        else{
            holder.txtOverdue.text= context.getString(R.string.days_remaining)

        }
        holder.txtOverdue.setOnClickListener {
            listener.onItemClick(position)
        }
        holder.txtDays.text = "${item.daysRemaining}"

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDaysBetweenDates(startDate: String, endDate: String, dateFormat: String): Long {
        // Define the date formatter
        val formatter = DateTimeFormatter.ofPattern(dateFormat)

        // Parse the dates
        val start = LocalDate.parse(startDate, formatter)
        val end = LocalDate.parse(endDate, formatter)

        // Calculate the days between
        return ChronoUnit.DAYS.between(start, end)
    }

    override fun getItemCount() = itemList.size
}