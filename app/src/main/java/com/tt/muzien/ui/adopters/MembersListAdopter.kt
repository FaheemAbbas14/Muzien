package com.tt.muzien.ui.adopters

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
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tt.muzien.R
import com.tt.muzien.data.dto.MemberDto
import com.zabihah.ui.ui.interfaces.OnItemClickListner
import de.hdodenhof.circleimageview.CircleImageView


/**
 * Created by Faheem Abbas on 03/12/2024.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class MembersListAdopter(
    private val itemList: List<MemberDto>,
    private val context: Context,
    private val listener: OnItemClickListner,
) :
    RecyclerView.Adapter<MembersListAdopter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val imgProfilePic: CircleImageView = itemView.findViewById(R.id.imgProfilePic)
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val llStatus: LinearLayout = itemView.findViewById(R.id.llStatus)
        val txtStatusTexts: TextView = itemView.findViewById(R.id.txtStatusTexts)
        val txtProfesstion: TextView = itemView.findViewById(R.id.txtProfesstion)
        val txtRating: TextView = itemView.findViewById(R.id.txtRating)
        val txtStyle: TextView = itemView.findViewById(R.id.txtStyle)
        val txtBookings: TextView = itemView.findViewById(R.id.txtBookings)
        val imgMenu: ImageView = itemView.findViewById(R.id.imageView4)

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
            .inflate(R.layout.member_list_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var item: MemberDto = itemList[position]

        holder.txtName.text = item.name
        holder.txtProfesstion.text = item.profession
        holder.txtRating.text = item.rating
        holder.txtStyle.text = item.style
        if (item.status) {
            holder.llStatus.setBackgroundDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.rounded_green,
                    context.theme
                )
            )
            holder.txtStatusTexts.text = "working today"
        } else {
            holder.llStatus.setBackgroundDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.rounded_red,
                    context.theme
                )
            )
            holder.txtStatusTexts.text = "on leave today"
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
                holder.imgProfilePic.scaleType = ImageView.ScaleType.CENTER_INSIDE
                Log.d("imageLoaded", "failed ${item.name}")
                return false
            }


        }
        Glide.with(holder.imgProfilePic)
            .load(item.profilePic)
            .placeholder(R.drawable.user_placeholder)
            .listener(iconRequestListener)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // Cache both original & transformed image
            .skipMemoryCache(false)  // Cache in memory
            .into(holder.imgProfilePic)
    }


    override fun getItemCount() = itemList.size
}