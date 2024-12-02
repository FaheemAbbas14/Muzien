package com.tt.muzien.ui.bottomSheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tt.muzien.R
import com.tt.muzien.ui.home.HomeActivity

/**
 * Created by Faheem Abbas on 29/11/2024.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class AddBottomSheet(activity1: HomeActivity) : BottomSheetDialogFragment()  {
    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.add_bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtSaloon: TextView = view.findViewById(R.id.txtSaloon)
        val txtMember: TextView = view.findViewById(R.id.txtMember)
        val txtService: TextView = view.findViewById(R.id.txtService)
        // Set up button actions
        txtSaloon.setOnClickListener {
            // Dismiss the bottom sheet
            dismiss()
        }
        txtMember.setOnClickListener {
            // Dismiss the bottom sheet
            dismiss()
        }
        txtService.setOnClickListener {
            // Dismiss the bottom sheet
            dismiss()
        }


    }
}