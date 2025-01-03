package com.tt.muzien.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.tt.muzien.R
import com.tt.muzien.data.dto.ServiceSaloon

/**
 * Created by Faheem Abbas on 03/01/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class ServiceUpdateListAdopter(
    private val salons: List<ServiceSaloon>,
    private val onToggleChanged: (position: Int, isEnabled: Boolean) -> Unit
) : RecyclerView.Adapter<ServiceUpdateListAdopter.SalonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.saloon_data_item, parent, false)
        return SalonViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalonViewHolder, position: Int) {
        val salon = salons[position]
        holder.bind(salon)

        // Handle toggle switch listener
        holder.toggle.setOnCheckedChangeListener { _, isChecked ->
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
            onToggleChanged(position, isChecked)
        }
    }

    override fun getItemCount(): Int = salons.size

    class SalonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.salon_name)
        val address: TextView = itemView.findViewById(R.id.saloon_address)
        val durationSpinner: TextView = itemView.findViewById(R.id.edtDuration)
        val price: TextView = itemView.findViewById(R.id.edtPrice)
        val txtCurrency: TextView = itemView.findViewById(R.id.txtCurrency)
        val toggle: SwitchCompat = itemView.findViewById(R.id.salon_toggle)
        val duration_layout: LinearLayout = itemView.findViewById(R.id.duration_layout)
        val txtDurationLabel: TextView = itemView.findViewById(R.id.txtDurationLabel)
        val price_layout: LinearLayout = itemView.findViewById(R.id.price_layout)
        val txtPriceLabel: TextView = itemView.findViewById(R.id.txtPriceLabel)

        fun bind(salon: ServiceSaloon) {
            name.text = salon.name
            address.text = salon.address
            txtCurrency.text = salon.currency
            price.text = salon.price
            toggle.isChecked = salon.isEnabled
            durationSpinner.text = "${salon.duration}"
        }
    }
}