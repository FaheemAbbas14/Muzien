package com.tt.muzien.ui.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tt.muzien.R
import com.tt.muzien.data.dto.ServiceInfo
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

/**
 * Created by Faheem Abbas on 19/12/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class ExpandServiceListAdapter(
    private val context: Context,
    private val groupTitles: List<String>,
    private val childItems: Map<String, List<ServiceInfo>>,
    private val isFromMain: Boolean = true,
    private val servicesCount: List<String>? = null

) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = groupTitles.size

    override fun getChildrenCount(groupPosition: Int): Int {
        val groupTitle = groupTitles[groupPosition]
        return childItems[groupTitle]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): Any = groupTitles[groupPosition]
    fun getGroupServices(groupPosition: Int): Any = servicesCount?.get(groupPosition) ?: 0

    override fun getChild(groupPosition: Int, childPosition: Int): ServiceInfo? {
        val groupTitle = groupTitles[groupPosition]
        return childItems[groupTitle]?.get(childPosition)
    }

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = true

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var view = LayoutInflater.from(context).inflate(R.layout.main_service_item, parent, false)
        val constraintLayout4 = view.findViewById<LinearLayout>(R.id.constraintLayout4)
        val divider = view.findViewById<View>(R.id.divider)
        // Change background based on expanded/collapsed state
        if (isExpanded) {
            divider.visibility = View.GONE
            constraintLayout4.setBackgroundDrawable(context.resources.getDrawable(R.drawable.top_rounded_corners))
        } else {
            divider.visibility = View.VISIBLE
            constraintLayout4.setBackgroundDrawable(context.resources.getDrawable(R.drawable.white_rounded10))
        }
        val servicesCount = view.findViewById<TextView>(R.id.servicesCount)
        servicesCount.text = getGroupServices(groupPosition).toString()

        val groupTitle = view.findViewById<TextView>(R.id.group_title)
        val groupIcon = view.findViewById<ImageView>(R.id.group_icon)

        groupTitle.text = getGroup(groupPosition).toString()
        groupIcon.setImageResource(if (isExpanded) R.drawable.blue_up_icon else R.drawable.blue_down_icon)

        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view =
            convertView ?: LayoutInflater.from(context).inflate(R.layout.child_item, parent, false)
        val constraintLayout4 = view.findViewById<ConstraintLayout>(R.id.constraintLayout4)
        if (isLastChild) {
            constraintLayout4.setBackgroundDrawable(context.resources.getDrawable(R.drawable.white_bottom_rounded))
        }

        val divider = view.findViewById<View>(R.id.divider)
// Show divider only if this is the last child
        divider.visibility = if (isLastChild) View.VISIBLE else View.GONE
        val imgProfilePic = view.findViewById<ImageView>(R.id.imgProfilePic)
        val txtName = view.findViewById<TextView>(R.id.txtName)
        val txtDuration = view.findViewById<TextView>(R.id.txtDuration)
        val txtRate = view.findViewById<TextView>(R.id.txtRate)
        var childInfo = getChild(groupPosition, childPosition)
        txtName.text = childInfo?.name
        txtDuration.text = childInfo?.duration
        txtRate.text = childInfo?.rate
        // Implement the RequestListener here
        val iconRequestListener = object : RequestListener<Drawable> {

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                Log.d("imageLoaded", "success ${childInfo?.name}")

                imgProfilePic.scaleType = ImageView.ScaleType.CENTER_CROP
                return false
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                imgProfilePic.scaleType = ImageView.ScaleType.CENTER_INSIDE
                Log.d("imageLoaded", "failed ${childInfo?.name}")
                return false
            }


        }
        Glide.with(imgProfilePic)
            .load(childInfo?.icon)
            .transform(CenterCrop(), RoundedCornersTransformation(20, 0))
            .placeholder(R.drawable.service_placeholder)
            .listener(iconRequestListener)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)  // Cache both original & transformed image
            .skipMemoryCache(false)  // Cache in memory
            .into(imgProfilePic)
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}