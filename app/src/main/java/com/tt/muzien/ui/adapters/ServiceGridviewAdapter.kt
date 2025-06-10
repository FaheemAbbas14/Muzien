package com.tt.muzien.ui.adapters

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.tt.muzien.R
import com.zabihah.ui.ui.interfaces.OnItemClickListner


/**
 * Created by Faheem Abbas on 04/01/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class ServiceGridviewAdapter(
    private val items: List<String>,
    private val context: Context,
    private val listener: OnItemClickListner,
) : RecyclerView.Adapter<ServiceGridviewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val textView = TextView(context).apply {
            setBackgroundResource(R.drawable.rounded_white_grey_50)
            setTextColor(context.resources.getColor(R.color.black_shade1))
            textSize = 12f
            gravity = Gravity.CENTER
            layoutParams = FlexboxLayoutManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8) // set 8px margin on all sides
            }.apply {
                setPadding(16, 16, 16, 16)
            }
        }

        return ItemViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.textView.text = items[position]
        holder.textView.setOnClickListener{
            listener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int = items.size
}
