package com.tt.muzien.ui.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tt.muzien.R
import com.tt.muzien.data.SaloonBookingData
import com.zabihah.ui.ui.interfaces.OnItemClickListner


/**
 * Created by Faheem Abbas on 18/12/2024.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class SaloonBookingAdapter(
    private val itemList: List<SaloonBookingData>,
    private val context: Context,
    private val listener: OnItemClickListner,
    private var bookingStatus: String,
) :
    RecyclerView.Adapter<SaloonBookingAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val imgProfilePic: ImageView = itemView.findViewById(R.id.imgProfilePic)
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtStyle: TextView = itemView.findViewById(R.id.txtStyle)
        val txtUserName: TextView = itemView.findViewById(R.id.txtUserName)
        val txtService: TextView = itemView.findViewById(R.id.txtService)

        val llReview: CardView = itemView.findViewById(R.id.llReview)
        val imgItemIcon: ImageView = itemView.findViewById(R.id.imgItemIcon)
        val txtItemName: TextView = itemView.findViewById(R.id.txtItemName)
        val txtAddedOn: TextView = itemView.findViewById(R.id.txtAddedOn)
        val txtReview: TextView = itemView.findViewById(R.id.txtReview)
        val txtRatings: TextView = itemView.findViewById(R.id.txtRatings)
        val rcyPhotos: RecyclerView = itemView.findViewById(R.id.rcyPhotos)

        val llCancel: LinearLayout = itemView.findViewById(R.id.llCancel)
        val txtCancelBy: TextView = itemView.findViewById(R.id.txtCancelBy)
        val txtReason: TextView = itemView.findViewById(R.id.txtReason)

        val llPending: LinearLayout = itemView.findViewById(R.id.llPending)
        val txtReject: TextView = itemView.findViewById(R.id.txtReject)
        val txtApprove: TextView = itemView.findViewById(R.id.txtApprove)

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
            .inflate(R.layout.saloon_booking_item, parent, false)
        if (bookingStatus == "Overdue/Incomplete") {
            itemView.setBackgroundColor(context.resources.getColor(R.color.overduebg))
        }
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var item: SaloonBookingData = itemList[position]
        if (bookingStatus == "Completed") {
            holder.llReview.visibility = View.VISIBLE
        } else if (bookingStatus == "Cancelled") {
            holder.llCancel.visibility = View.VISIBLE
        } else if (bookingStatus == "Pending Approval") {
            holder.llPending.visibility = View.VISIBLE
        }
        holder.txtName.text = item.name
        holder.txtStyle.text = item.style
        holder.txtUserName.text = item.personName
        holder.txtService.text = item.service

        // Implement the RequestListener here
        val iconRequestListener = object : RequestListener<Drawable> {

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                Log.d("imageLoaded", "success ${item.name}")

                holder.imgProfilePic.scaleType = ImageView.ScaleType.CENTER_CROP
                return false
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                holder.imgProfilePic.scaleType = ImageView.ScaleType.CENTER_CROP
                Log.d("imageLoaded", "failed ${item.name}")
                return false
            }


        }
        Glide.with(holder.imgProfilePic)
            .load(item.imageUrl)
            .placeholder(R.drawable.topperformer)
            .circleCrop()
            .listener(iconRequestListener)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // Cache both original & transformed image
            .skipMemoryCache(false)  // Cache in memory
            .into(holder.imgProfilePic)
    }

    fun setBookingStatus(status: String) {
        bookingStatus = status
    }

    override fun getItemCount() = itemList.size
}