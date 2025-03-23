package com.tt.muzien.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tt.muzien.R
import com.tt.muzien.data.dto.WorkingHourData
import com.zabihah.ui.ui.interfaces.OnItemClickListner


/**
 * Created by Faheem Abbas on 26/12/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class WorkingHoursAdapter(
    private val itemList: List<WorkingHourData>,
    private val context: Context,
    private val listener: OnItemClickListner,
) :
    RecyclerView.Adapter<WorkingHoursAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val txtTiming: TextView = itemView.findViewById(R.id.txtTiming)
        val imgMinus: ImageView = itemView.findViewById(R.id.imgMinus)

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
            .inflate(R.layout.working_hour_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var item: WorkingHourData = itemList[position]

        holder.txtTitle.text = item.title
        holder.txtTiming.text = item.hours
        holder.imgMinus.setOnClickListener {
            listener.onItemClick(position)
        }
    }


    override fun getItemCount() = itemList.size
}