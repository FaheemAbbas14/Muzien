package com.tt.muzien.ui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.tt.muzien.R


/**
 * Created by Faheem Abbas on 04/06/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class ServiceSpinnerAdapter(
    context: Context,
    private val items: List<String>
) : ArrayAdapter<String>(context, R.layout.service_spinner_item, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.service_spinner_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.textViewItem)

        val item = items[position]
        textView.text = item

        // Gray text for placeholder
        if (position == 0) {
            textView.setTextColor(Color.GRAY)
        } else {
            textView.setTextColor(Color.BLACK)
        }

        return view
    }
}