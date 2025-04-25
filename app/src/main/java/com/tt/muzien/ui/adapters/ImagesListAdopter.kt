package com.tt.muzien.ui.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.tt.muzien.R
import com.zabihah.ui.ui.interfaces.OnItemClickListner
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


/**
 * Created by Faheem Abbas on 23/03/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class ImagesListAdopter(
    private val itemList: List<Uri>,
    private val listener: OnItemClickListner,
) :
    RecyclerView.Adapter<ImagesListAdopter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val imgPhoto: ImageView = itemView.findViewById(R.id.imgPhoto)
        val imgDelete: ImageView = itemView.findViewById(R.id.imgDelete)

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
            .inflate(R.layout.images_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var item: Uri = itemList[position]
        Glide.with(holder.imgPhoto)
            .load(item)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // Cache both original & transformed image
            .skipMemoryCache(false)  // Cache in memory
            .into(holder.imgPhoto)
        holder.imgDelete.setOnClickListener {
            listener.onItemClick(position)
        }

    }


    override fun getItemCount() = itemList.size
}