package com.tt.muzien.ui.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
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
    private val onToggleChanged: (position: Int, isEnabled: Boolean) -> Unit
) :
    RecyclerView.Adapter<ServiceUpdateListAdopter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.salon_name)
        val address: TextView = itemView.findViewById(R.id.saloon_address)
        val durationSpinner: EditText = itemView.findViewById(R.id.edtDuration)
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
         holder.toggle.isChecked = salon.isEnabled
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
        holder.name.text = salon.name
        holder.address.text = salon.address
        holder.txtCurrency.text = salon.currency
        holder.edtPrice.text = Editable.Factory.getInstance().newEditable(salon.price)
        holder.durationSpinner.text = Editable.Factory.getInstance().newEditable(salon.duration)
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
        holder.durationSpinner.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                salons[holder.adapterPosition].duration = s.toString()
                //checkValidation()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        holder.edtPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                salons[holder.adapterPosition].price = s.toString()
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