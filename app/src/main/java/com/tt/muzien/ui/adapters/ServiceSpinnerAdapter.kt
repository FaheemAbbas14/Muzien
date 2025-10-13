package com.tt.muzien.ui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.TextView
import com.tt.muzien.R

class ServiceSpinnerAdapter(
    private val context: Context,
    private val items: List<String>,
    private val placeHolder: Boolean=true
) : ArrayAdapter<String>(context, R.layout.service_spinner_item, items) {

    // Keep track of which item is selected
    private var selectedPosition: Int = 0

    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Collapsed view (what you see on the spinner itself)
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.service_spinner_item, parent, false)
        val tv = view.findViewById<TextView>(R.id.textViewItem)

        val item = items[position]
        tv.text = item

        // Gray text for placeholder
        if (placeHolder && position == 0) {
            tv.setTextColor(Color.GRAY)
        } else {
            tv.setTextColor(Color.BLACK)
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Dialog/dropdown list row with RadioButton
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.service_spinner_dropdown_item, parent, false)

        val tv = view.findViewById<TextView>(R.id.textViewItem)
        val rb = view.findViewById<RadioButton>(R.id.radioButton)
        val divider = view.findViewById<View>(R.id.rowDivider)
        val item = items[position]
        tv.text = item

        // Placeholder gray
        if (placeHolder && position == 0) {
           // view.visibility=View.GONE
            rb.visibility=View.INVISIBLE
            tv.setTextColor(Color.GRAY)
        } else {
            tv.setTextColor(Color.BLACK)
        }
        // hide divider on the last row
        divider.visibility = if (position == count - 1) View.GONE else View.VISIBLE
        // Check radio for the currently selected item
        rb.isChecked = position == selectedPosition

        return view
    }
}
