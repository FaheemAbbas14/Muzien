package com.tt.muzien.ui.adapters

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
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
    private var isFromMain: Boolean = false
) :
    RecyclerView.Adapter<SaloonBookingAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val mainCard: CardView = itemView.findViewById(R.id.mainCard)
        val imgProfilePic: ImageView = itemView.findViewById(R.id.imgProfilePic)
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtStyle: TextView = itemView.findViewById(R.id.txtStyle)
        val txtUserName: TextView = itemView.findViewById(R.id.txtUserName)
        val txtService: TextView = itemView.findViewById(R.id.txtService)

        val llReview: ConstraintLayout = itemView.findViewById(R.id.llReview)
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

        val llMainView: ConstraintLayout = itemView.findViewById(R.id.llMainView)
        val txtCancel: TextView = itemView.findViewById(R.id.txtCancel)
        val imgBarCode: ImageView = itemView.findViewById(R.id.imgBarCode)

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
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            var item: SaloonBookingData = itemList[position]
            if (bookingStatus == "Completed") {
                holder.llReview.visibility = View.VISIBLE
            } else if (bookingStatus == "Cancelled") {
                holder.llCancel.visibility = View.VISIBLE
            } else if (bookingStatus == "Pending Approval") {
                holder.llPending.visibility = View.VISIBLE
            } else if (bookingStatus == "Overdue/Incomplete") {
                holder.mainCard.setBackgroundDrawable(context.resources.getDrawable(R.drawable.rounded_overdue))
            }
            if (isFromMain) {
                holder.llMainView.visibility = View.VISIBLE
            }
            holder.txtName.text = item.name
            holder.txtStyle.text = item.style
            holder.txtUserName.text = item.personName
            holder.txtService.text = item.service
            holder.txtCancel.setOnClickListener {
                showPopupDialog()
            }
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

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBookingStatus(status: String) {
        bookingStatus = status
    }

    private fun showPopupDialog() {
        // Create Dialog
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.cancel_booking, null)
        dialog.setContentView(view)

        // Make dialog background transparent
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Find buttons and handle click events
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val proceedButton = view.findViewById<Button>(R.id.proceed_button)

        btnCancel.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog
        }

        proceedButton.setOnClickListener {
            // Add your logic here (e.g., enable the service)
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }

    override fun getItemCount() = itemList.size
}