package com.tt.muzien.ui.profile

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tt.muzien.R
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.ui.saloon.FragmentAddSaloon
import java.util.Locale


class FragmentChangeLanguage() : BottomSheetDialogFragment() {
    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_language, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerLanguage: Spinner = view.findViewById(R.id.spinnerLanguage)
        val btnConfirm: Button = view.findViewById(R.id.btnConfirm)
        // Set up button actions
        btnConfirm.setOnClickListener {
            var language=spinnerLanguage.selectedItem
            if (language=="Arabic"){
                setLocale(requireContext(),"ar")
            }
            else{
                setLocale(requireContext(),"en")
            }
            (activity as HomeActivity?)?.popFragment()
            // Dismiss the bottom sheet
            dismiss()
        }


    }
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}