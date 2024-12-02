package com.tt.muzien.ui.adopters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tt.muzien.R
import com.tt.muzien.data.dto.PersonDto
import de.hdodenhof.circleimageview.CircleImageView


/**
 * Created by Faheem Abbas on 28/11/2024.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class PerformerListAdopter(
    private val itemList: List<PersonDto>,
    private val context: Context,
) :
    RecyclerView.Adapter<PerformerListAdopter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfilePic: CircleImageView = itemView.findViewById(R.id.imgProfilePic)
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtProfesstion: TextView = itemView.findViewById(R.id.txtProfesstion)
        val txtBookings: TextView = itemView.findViewById(R.id.txtBookings)
        val txtRate: TextView = itemView.findViewById(R.id.txtRate)

//        init {
//            itemView.setOnClickListener(this)
//        }
//
//        override fun onClick(v: View?) {
//            val position = adapterPosition
//            if (position != RecyclerView.NO_POSITION) {
//                listener.onItemClick(position)
//            }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.performer_list_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var item: PersonDto = itemList[position]

        holder.txtName.text = item.name
        holder.txtProfesstion.text = item.profession
        holder.txtBookings.text = item.booking
        holder.txtRate.text = item.rate
        Glide.with(holder.imgProfilePic)
            .load(item.profilePicUrl)
            .placeholder(R.drawable.user_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // Cache both original & transformed image
            .skipMemoryCache(false)  // Cache in memory
            .into(holder.imgProfilePic)
    }


    override fun getItemCount() = itemList.size
}