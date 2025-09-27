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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tt.muzien.R
import com.tt.muzien.data.dto.SaloonDto
import com.tt.muzien.utilities.TimeHelper
import com.zabihah.ui.ui.interfaces.OnItemClickListner


/**
 * Created by Faheem Abbas on 03/12/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class SaloonListAdapter(
    private val itemList: List<SaloonDto>,
    private val context: Context,
    private val listener: OnItemClickListner,
) :
    RecyclerView.Adapter<SaloonListAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val imgIcon: ImageView = itemView.findViewById(R.id.imgIcon)
        val txtItemName: TextView = itemView.findViewById(R.id.txtItemName)
        val llStatus: LinearLayout = itemView.findViewById(R.id.llStatus)
        val txtStatusTexts: TextView = itemView.findViewById(R.id.txtStatusTexts)
        val txtLocation: TextView = itemView.findViewById(R.id.txtLocation)
        val txtRating: TextView = itemView.findViewById(R.id.txtRating)
        val txtTiming: TextView = itemView.findViewById(R.id.txtTiming)
        val llTimes: LinearLayout = itemView.findViewById(R.id.llTimes)
        val cnstMain: ConstraintLayout = itemView.findViewById(R.id.cnstMain)

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
            .inflate(R.layout.saloon_list_item, parent, false)

        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var item: SaloonDto = itemList[position]

        holder.txtItemName.text = item.name
        holder.txtLocation.text = item.location
        holder.txtRating.text = item.ratings
        var timing = TimeHelper.getCurrentDayTiming(item.timing)
        holder.txtTiming.text = timing
        if (item.isActive) {
            if (item.isOpened) {
                holder.llStatus.setBackgroundDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.rounded_green,
                        context.theme
                    )
                )
                holder.txtStatusTexts.text = context.resources.getString(R.string.open)
                holder.llTimes.visibility = View.VISIBLE
                holder.cnstMain.setBackgroundColor(context.resources.getColor(R.color.white))
            } else {
                holder.llStatus.setBackgroundDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.rounded_red,
                        context.theme
                    )
                )
                holder.txtStatusTexts.text = context.resources.getString(R.string.closed)
                holder.llTimes.visibility = View.GONE
                holder.cnstMain.setBackgroundColor(context.resources.getColor(R.color.white))
            }
        } else {

            holder.llStatus.setBackgroundDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.rounded_red,
                    context.theme
                )
            )
            holder.txtStatusTexts.text = context.resources.getString(R.string.inactive)
            holder.llTimes.visibility = View.GONE
            holder.cnstMain.setBackgroundColor(context.resources.getColor(R.color.overduebg))

        }
        item.icon?.size?.let {
            if (it > 0) {
                // Implement the RequestListener here
                val iconRequestListener = object : RequestListener<Drawable> {

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean,
                    ): Boolean {
                        Log.d("imageLoaded", "success ${item.name}")

                        holder.imgIcon.scaleType = ImageView.ScaleType.CENTER_CROP
                        return false
                    }

                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean,
                    ): Boolean {
                        holder.imgIcon.scaleType = ImageView.ScaleType.CENTER_CROP
                        Log.d("imageLoaded", "failed ${item.name}")
                        return false
                    }


                }
                Glide.with(holder.imgIcon)
                    .load(item.icon[0]?.image)
                    .placeholder(R.drawable.salonplaceholder)
                    .listener(iconRequestListener)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // Cache both original & transformed image
                    .skipMemoryCache(false)  // Cache in memory
                    .into(holder.imgIcon)
            }
        }
    }


    override fun getItemCount() = itemList.size
}