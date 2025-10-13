package com.tt.muzien.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tt.muzien.R
import com.tt.muzien.data.dto.NotificationDto
import com.tt.muzien.enums.EnumNotificationType
import com.zabihah.ui.ui.interfaces.OnItemClickListner


/**
 * Created by Faheem Abbas on 03/12/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class NotificationListAdapter(
    private val itemList: List<NotificationDto>,
    private val context: Context,
    private val listener: OnItemClickListner,
) :
    RecyclerView.Adapter<NotificationListAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val imgSeen: ImageView = itemView.findViewById(R.id.imgSeen)
        val imgProfilePic: ImageView = itemView.findViewById(R.id.imgProfilePic)
        val txtHeading: TextView = itemView.findViewById(R.id.txtHeading)
        val txtDescription: TextView = itemView.findViewById(R.id.txtDescription)
        val imgNext: ImageView = itemView.findViewById(R.id.imageView7)
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
            .inflate(R.layout.category_notification_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var item: NotificationDto = itemList[position]

        holder.txtHeading.text = item.heading
        holder.txtDescription.text = item.description
        if (item.enumType == EnumNotificationType.Category) {
            holder.imgNext.visibility = View.VISIBLE
            holder.divider.visibility = View.VISIBLE
            var icon=R.drawable.salon_icon
            if (item.type.contains("invite")){
                icon=R.drawable.invites_icon
            }
            else if (item.type.contains("booking")){
                icon=R.drawable.booking_reminder
            }

            Glide.with(holder.imgProfilePic)
                .load(icon)
                .placeholder(icon)
                .into(holder.imgProfilePic)

        } else {
            holder.imgNext.visibility = View.GONE
            holder.divider.visibility = View.GONE
            Glide.with(holder.imgProfilePic)
                .load(item.profileUrl)
                .circleCrop()
                .placeholder(R.drawable.user_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // Cache both original & transformed image
                .skipMemoryCache(false)  // Cache in memory
                .into(holder.imgProfilePic)
        }
        if (item.isSeen) {
            holder.imgSeen.visibility = View.INVISIBLE
        } else {
            holder.imgSeen.visibility = View.VISIBLE
        }
    }


    override fun getItemCount() = itemList.size
}