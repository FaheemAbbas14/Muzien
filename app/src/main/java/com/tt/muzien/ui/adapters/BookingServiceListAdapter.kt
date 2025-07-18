package com.tt.muzien.ui.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tt.muzien.R
import com.tt.muzien.data.dto.BookingServiceDto
import com.zabihah.ui.ui.interfaces.OnItemClickListner


/**
 * Created by Faheem Abbas on 17/04/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class BookingServiceListAdapter(
    private val itemList: List<BookingServiceDto>,
    private val context: Context,
    private val listener: OnItemClickListner,
) :
    RecyclerView.Adapter<BookingServiceListAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val txtName: TextView = itemView.findViewById(R.id.textView6)
        val txtDuration: TextView = itemView.findViewById(R.id.textView11)
        val txtAmount: TextView = itemView.findViewById(R.id.textView16)
        val divider: TextView = itemView.findViewById(R.id.divider)

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
            .inflate(R.layout.booking_service_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var item: BookingServiceDto = itemList[position]
        if (position == itemList.size - 1) {
            holder.divider.visibility = View.GONE
            holder.txtName.setTypeface(holder.txtName.typeface, Typeface.BOLD)
        }
        holder.txtName.text = item.name
        holder.txtAmount.text = item.amount
        holder.txtDuration.text = "Duration ${item.duration}"

    }


    override fun getItemCount() = itemList.size
}