package com.tt.muzien.ui.profile

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tt.muzien.R
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.utilities.PreferenceManager
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
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_options,
            R.layout.spinner_item // custom layout
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerLanguage.adapter = adapter
        }
        val btnConfirm: Button = view.findViewById(R.id.btnConfirm)
        // Set up button actions
        btnConfirm.setOnClickListener {
            var language = spinnerLanguage.selectedItem
            if (language == "Arabic") {
                changeLanguage(requireContext(), "ar")
            } else {
                changeLanguage(requireContext(), "en")
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

    fun changeLanguage(context: Context, newLanguage: String) {
        PreferenceManager.getInstance(context).saveLanguage(newLanguage)
        setLocale(context, newLanguage)

        val intent = (context as? Activity)?.intent
        intent?.let {
            context.finish()
            context.startActivity(it)
        }
    }
}