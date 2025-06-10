package com.tt.muzien.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.tt.muzien.R
import com.zabihah.ui.ui.interfaces.OnItemClickListner


/**
 * Created by Faheem Abbas on 04/06/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class SelectedServiceAdaptor(
    private val itemList: List<String>,
    private val listener: OnItemClickListner,
) :
    RecyclerView.Adapter<SelectedServiceAdaptor.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val remove: ImageView = itemView.findViewById(R.id.imageView17)
        val selected: TextView = itemView.findViewById(R.id.textView24)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.selected_service_item, parent, false)

        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var item: String = itemList[position]

        holder.selected.text = item
        holder.remove.setOnClickListener {
            listener.onItemClick(position)
        }

    }


    override fun getItemCount() = itemList.size
}