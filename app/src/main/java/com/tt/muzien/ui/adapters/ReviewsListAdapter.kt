package com.tt.muzien.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.tt.muzien.R
import com.tt.muzien.data.dto.ReviewsInfo


/**
 * Created by Faheem Abbas on 05/08/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class ReviewsListAdapter(
    private val itemList: List<ReviewsInfo>,
    private val context: Context,
) :
    RecyclerView.Adapter<ReviewsListAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtItemName: TextView = itemView.findViewById(R.id.txtItemName)
        val txtAddedOn: TextView = itemView.findViewById(R.id.txtAddedOn)
        val imgItemIcon: ImageView = itemView.findViewById(R.id.imgItemIcon)

        /// val rating: RatingBar = itemView.findViewById(R.id.ratingBar)
        val txtProvidedBy: TextView = itemView.findViewById(R.id.txtProvidedBy)
        val txtReview: TextView = itemView.findViewById(R.id.txtReview)
        val txtRatings: TextView = itemView.findViewById(R.id.txtRatings)
        val rcyPhotos: RecyclerView = itemView.findViewById(R.id.rcyPhotos)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.review_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var item: ReviewsInfo = itemList[position]
        holder.txtItemName.text = item.name
        holder.txtAddedOn.text = item.addedOn
        holder.txtProvidedBy.text = item.providedBy
        holder.txtReview.text = item.review
        holder.txtRatings.text = item.rating
        Glide.with(holder.imgItemIcon)
            .load(item.icon)
            .placeholder(R.drawable.topperformer)
            .transform(CircleCrop())
            .override(100, 100)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // Cache both original & transformed image
            .skipMemoryCache(false)  // Cache in memory
            .into(holder.imgItemIcon)
        setPhotosAdopter(item.images, holder.rcyPhotos)


    }

    private fun setPhotosAdopter(photosList: List<String?>, rcyPhotos: RecyclerView) {


        rcyPhotos.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rcyPhotos.adapter =
            PhotosListAdapter(photosList, context, null)

    }


    override fun getItemCount() = itemList.size
}