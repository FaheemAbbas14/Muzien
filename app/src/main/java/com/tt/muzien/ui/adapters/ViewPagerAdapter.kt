package com.tt.muzien.ui.adapters

import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tt.muzien.R


/**
 * Created by Faheem Abbas on 31/12/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class ViewPagerAdapter (private val items: List<String>) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgImage: ImageView = itemView.findViewById(R.id.imageView15)
        fun bind(data: String) {
// Implement the RequestListener here
            val iconRequestListener = object : RequestListener<Drawable> {

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("imageLoaded", "success ${data}")

                    //  holder.imgProfilePic.scaleType = ImageView.ScaleType.CENTER_CROP
                    return false
                }

                @RequiresApi(Build.VERSION_CODES.M)
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
//                holder.imgProfilePic.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    Log.d("imageLoaded", "failed ${data}")
                    return false
                }


            }
            Glide.with(imgImage)
                .load(data)
                .listener(iconRequestListener)
                .placeholder(R.drawable.salonplaceholder)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // Cache both original & transformed image
                .skipMemoryCache(false)  // Cache in memory
                .into(imgImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_pager_images, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}