package com.tt.muzien.ui.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.tt.muzien.R


/**
 * Created by Faheem Abbas on 04/01/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class ServiceGridviewAdapter(private val items: List<String>,val context: Context) : BaseAdapter() {
    override fun getCount(): Int = items.size
    override fun getItem(position: Int): Any = items[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView: TextView = convertView as? TextView ?: TextView(parent?.context).apply {
            setPadding(16, 16, 16, 16)
            textSize = 16f
            setBackgroundDrawable(context.resources.getDrawable(R.drawable.rounded_white_grey_50))
            setTextColor(context.resources.getColor(R.color.black_shade1)) // Black
            setTextSize(12f)
            gravity = android.view.Gravity.CENTER
        }
        textView.text = items[position]
        return textView
    }
}
