package com.tt.muzien.ui.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.tt.muzien.R
import com.tt.muzien.data.dto.ServiceSaloon

/**
 * Created by Faheem Abbas on 03/01/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
class ServiceUpdateListAdopter(
    private val salons: List<ServiceSaloon>,
    private val context: Context,
    private val onToggleChanged: (position: Int, isEnabled: Boolean) -> Unit,

    ) :
    RecyclerView.Adapter<ServiceUpdateListAdopter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.salon_name)
        val address: TextView = itemView.findViewById(R.id.saloon_address)
        val spinnerDuration: Spinner = itemView.findViewById(R.id.spinnerDuration)
        val ddlDuration: ImageView = itemView.findViewById(R.id.ddlDuration)
        val edtPrice: EditText = itemView.findViewById(R.id.edtPrice)
        val txtCurrency: TextView = itemView.findViewById(R.id.txtCurrency)
        val toggle: SwitchCompat = itemView.findViewById(R.id.salon_toggle)
        val duration_layout: LinearLayout = itemView.findViewById(R.id.duration_layout)
        val txtDurationLabel: TextView = itemView.findViewById(R.id.txtDurationLabel)
        val price_layout: LinearLayout = itemView.findViewById(R.id.price_layout)
        val txtPriceLabel: TextView = itemView.findViewById(R.id.txtPriceLabel)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.saloon_data_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val salon = salons[position]
        val durations = listOf("15 mins", "30 mins", "45 mins", "60 mins", "75 mins", "90 mins", "105 mins", "120 mins")
        val adapter = ServiceSpinnerAdapter(context, durations,false)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinnerDuration.adapter = adapter
        holder.spinnerDuration.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    var duration = durations[position]
                    if (salons[holder.adapterPosition].duration != duration.replace(" mins", "")) {
                        salons[holder.adapterPosition].duration = duration.replace(" mins", "")
                        onToggleChanged.invoke(position, salon.isEnabled)
                    }
                    // Toast.makeText(requireContext(), "Selected: $duration", Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        holder.ddlDuration.setOnClickListener {
            holder.spinnerDuration.performClick()
        }
        // Avoid triggering listener when recycling
        holder.toggle.setOnCheckedChangeListener(null)
        holder.toggle.isChecked = salon.isEnabled

        // Set listener AFTER setting checked state
        holder.toggle.setOnCheckedChangeListener { _, isChecked ->
            salon.isEnabled = isChecked
            onToggleChanged.invoke(position, isChecked)
            if (isChecked) {
                holder.duration_layout.visibility = View.VISIBLE
                holder.txtDurationLabel.visibility = View.VISIBLE
                holder.price_layout.visibility = View.VISIBLE
                holder.txtPriceLabel.visibility = View.VISIBLE
            } else {
                holder.duration_layout.visibility = View.GONE
                holder.txtDurationLabel.visibility = View.GONE
                holder.price_layout.visibility = View.GONE
                holder.txtPriceLabel.visibility = View.GONE
            }
        }
        holder.name.text = salon.name
        holder.address.text = salon.address
        holder.txtCurrency.text = salon.currency
        holder.edtPrice.text = Editable.Factory.getInstance().newEditable(salon.price)
        var index = durations.indexOf("${salon.duration} mins")
        holder.spinnerDuration.setSelection(index)
        if (salon.isEnabled) {
            holder.duration_layout.visibility = View.VISIBLE
            holder.txtDurationLabel.visibility = View.VISIBLE
            holder.price_layout.visibility = View.VISIBLE
            holder.txtPriceLabel.visibility = View.VISIBLE
        } else {
            holder.duration_layout.visibility = View.GONE
            holder.txtDurationLabel.visibility = View.GONE
            holder.price_layout.visibility = View.GONE
            holder.txtPriceLabel.visibility = View.GONE
        }

        // ✅ Fix: Ensure EditText scrolls into view when focused
        holder.edtPrice.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // Find the NestedScrollView parent
                var parentView = holder.itemView.parent
                while (parentView != null && parentView !is androidx.core.widget.NestedScrollView) {
                    parentView = (parentView as? View)?.parent
                }

                val nestedScrollView = parentView as? androidx.core.widget.NestedScrollView
                nestedScrollView?.post {
                    nestedScrollView.smoothScrollTo(0, holder.itemView.top + holder.edtPrice.top)
                }
            }
        }
        holder.edtPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) {

            }


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (salons[holder.adapterPosition].price != s.toString()) {
                    salons[holder.adapterPosition].price = s.toString()
                    onToggleChanged.invoke(position, salon.isEnabled)
                }
                //checkValidation()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }


    override fun getItemCount() = salons.size
    fun getData(): List<ServiceSaloon> {
        return salons
    }
}