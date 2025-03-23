package com.tt.muzien.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tt.muzien.R
import com.zabihah.ui.ui.interfaces.OnItemClickListner


/**
 * Created by Faheem Abbas on 26/01/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class HolidayListAdapter(
    private val itemList: List<String>,
    private val isService: Boolean,
    private val listener: OnItemClickListner,
) :
    RecyclerView.Adapter<HolidayListAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val imgMinus: ImageView = itemView.findViewById(R.id.imgMinus)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                // listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.holiday_item, parent, false)
        if (isService) {
            itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.service_item, parent, false)
        }
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var item: String = itemList[position]

        holder.txtTitle.text = item
        holder.imgMinus.setOnClickListener {
            listener.onItemClick(position)
        }
    }


    override fun getItemCount() = itemList.size
}