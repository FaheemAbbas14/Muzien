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
import android.widget.PopupWindow
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
import com.tt.muzien.data.dto.MemberDto
import com.tt.muzien.interfaces.OnStateChange
import com.tt.muzien.ui.enable
import com.zabihah.ui.ui.interfaces.OnItemClickListner


/**
 * Created by Faheem Abbas on 03/12/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class MembersListAdapter(
    private val itemList: List<MemberDto>,
    private val context: Context,
    private val listener: OnItemClickListner,
    private val stateChange: OnStateChange,
    private val fromSaloon: Boolean = false,
) :
    RecyclerView.Adapter<MembersListAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val mainLayout: ConstraintLayout = itemView.findViewById(R.id.mainLayout)
        val imgProfilePic: ImageView = itemView.findViewById(R.id.imgProfilePic)
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val llStatus: LinearLayout = itemView.findViewById(R.id.llStatus)
        val txtStatusTexts: TextView = itemView.findViewById(R.id.txtStatusTexts)
        val txtProfesstion: TextView = itemView.findViewById(R.id.txtProfesstion)
        val txtRating: TextView = itemView.findViewById(R.id.txtRating)
        val txtStyle: TextView = itemView.findViewById(R.id.txtStyle)
        val txtBookings: TextView = itemView.findViewById(R.id.txtBookings)
        val txtManager: TextView = itemView.findViewById(R.id.txtManager)
        val imgMenu: ImageView = itemView.findViewById(R.id.imageView4)
        val llStyle: LinearLayout = itemView.findViewById(R.id.llStyle)

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
        holder.imgMenu.setOnClickListener {
            showCustomMenu(holder.imgMenu, stateChange, position, item.isMember,item.isManger)
        }
        if (fromSaloon) {
            holder.llStyle.visibility = View.GONE
        }

        holder.txtName.text = item.name
        holder.txtBookings.text = "${item.bookings ?: 0} bookings today"
        holder.txtProfesstion.text = item.profession
       // holder.txtProfesstion.visibility = View.GONE
        holder.txtRating.text = item.rating
        holder.txtStyle.text = item.style
        if (item.isManger) {
            holder.txtManager.visibility = View.VISIBLE
        } else {
            holder.txtManager.visibility = View.GONE
        }
        if (item.isMember) {
            if (item.status) {
                holder.llStatus.setBackgroundDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.green_70_rounded,
                        context.theme
                    )
                )
                holder.txtStatusTexts.text = context.resources.getString(R.string.working_today)
            } else {
                holder.llStatus.setBackgroundDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.red_70_rounded,
                        context.theme
                    )
                )
                holder.txtStatusTexts.text = context.resources.getString(R.string.on_leave)
            }
        } else {
            holder.llStatus.setBackgroundDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.rounded_blue,
                    context.theme
                )
            )
            holder.txtStatusTexts.text = context.resources.getString(R.string.invitation_sent)
            holder.mainLayout.alpha = 0.5f
            holder.itemView.setOnClickListener(null)
        }
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

                holder.imgProfilePic.scaleType = ImageView.ScaleType.CENTER_CROP
                return false
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean,
            ): Boolean {
                holder.imgProfilePic.scaleType = ImageView.ScaleType.CENTER_INSIDE
                Log.d("imageLoaded", "failed ${item.name}")
                return false
            }


        }
        Glide.with(holder.imgProfilePic)
            .load(item.profilePic)
            .circleCrop()
            .placeholder(R.drawable.topperformer)
            .listener(iconRequestListener)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // Cache both original & transformed image
            .skipMemoryCache(false)  // Cache in memory
            .into(holder.imgProfilePic)
    }

    private fun showCustomMenu(
        anchor: View,
        stateChange: OnStateChange,
        position: Int,
        isMember: Boolean,
        isManger: Boolean,
    ) {

        val inflater = LayoutInflater.from(anchor.context)
        val menuView = inflater.inflate(R.layout.invite_layout, null)

        val popupWindow = PopupWindow(
            menuView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val option1: TextView = menuView.findViewById(R.id.invite_salon)
        val option2: TextView = menuView.findViewById(R.id.invite_user)
        val option3: TextView = menuView.findViewById(R.id.delete_user)
        val option4: TextView = menuView.findViewById(R.id.delete_Invitation)
        val option5: TextView = menuView.findViewById(R.id.remove_manager)

        option1.text = "Mark as Manager"
        option2.text = "Inactivate User"
        option3.text = "Delete User"

        if (isMember) {
            option4.visibility = View.GONE
            option2.visibility = View.VISIBLE
            option3.visibility = View.VISIBLE

            if (isManger) {
                option5.visibility = View.VISIBLE
                option1.visibility = View.GONE
            } else {
                option1.visibility = View.VISIBLE
                option5.visibility = View.GONE
            }
        } else {
            option1.visibility = View.GONE
            option2.visibility = View.GONE
            option3.visibility = View.GONE
            option4.visibility = View.VISIBLE
            option5.visibility = View.GONE
        }

        option1.setOnClickListener { stateChange.onStateChange(position, 1); popupWindow.dismiss() }
        option2.setOnClickListener { stateChange.onStateChange(position, 2); popupWindow.dismiss() }
        option3.setOnClickListener { stateChange.onStateChange(position, 3); popupWindow.dismiss() }
        option4.setOnClickListener { stateChange.onStateChange(position, 4); popupWindow.dismiss() }
        option5.setOnClickListener { stateChange.onStateChange(position, 5); popupWindow.dismiss() }

        // ---- 🔑 Positioning logic ----
        menuView.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        val popupHeight = menuView.measuredHeight

        val location = IntArray(2)
        anchor.getLocationOnScreen(location)
        val anchorY = location[1]
        val anchorHeight = anchor.height
        val screenHeight = anchor.context.resources.displayMetrics.heightPixels

        val spaceBelow = screenHeight - (anchorY + anchorHeight)
        val spaceAbove = anchorY

        if (spaceBelow < popupHeight && spaceAbove > popupHeight) {
            popupWindow.showAsDropDown(anchor, 0, -(popupHeight + anchorHeight))
        } else {
            popupWindow.showAsDropDown(anchor, 0, 10)
        }
    }


    override fun getItemCount() = itemList.size
}