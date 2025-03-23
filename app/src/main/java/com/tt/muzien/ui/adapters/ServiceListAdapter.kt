package com.tt.muzien.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tt.muzien.R
import com.tt.muzien.data.dto.ServiceDto
import com.zabihah.ui.ui.interfaces.OnItemClickListner


/**
 * Created by Faheem Abbas on 03/12/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class ServiceListAdapter(
    private val itemList: List<ServiceDto>,
    private val context: Context,
    private val listener: OnItemClickListner,
) :
    RecyclerView.Adapter<ServiceListAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtServices: TextView = itemView.findViewById(R.id.txtServices)

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
            .inflate(R.layout.service_list_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var item: ServiceDto = itemList[position]

        holder.txtName.text = item.name
        holder.txtServices.text = "${item.services} Services"

    }


    override fun getItemCount() = itemList.size
}