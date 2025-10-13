package com.tt.muzien.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tt.muzien.R
import com.tt.muzien.ui.adapters.ServiceSpinnerAdapter
import com.tt.muzien.ui.home.HomeActivity
import com.tt.muzien.utilities.PreferenceManager
import java.util.Locale


class FragmentChangeLanguage() : BottomSheetDialogFragment() {
    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_language, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerLanguage: Spinner = view.findViewById(R.id.spinnerLanguage)
        var items = ArrayList<String>()
        items.add(resources.getString(R.string.english))
        items.add(resources.getString(R.string.arabic))
        val adapter = ServiceSpinnerAdapter(requireContext(), items, false)
        spinnerLanguage.adapter = adapter
        val isArabic = Locale.getDefault().language == "ar"
        if (isArabic) {
            adapter.setSelectedPosition(1)
        } else {
            adapter.setSelectedPosition(0)
        }
        val btnConfirm: Button = view.findViewById(R.id.btnConfirm)
        // Set up button actions
        btnConfirm.setOnClickListener {
            var language = spinnerLanguage.selectedItem
            adapter.setSelectedPosition(spinnerLanguage.selectedItemPosition)
            if (language == "Arabic") {
                changeLanguage(requireActivity(), "ar")
            } else {
                changeLanguage(requireActivity(), "en")
            }
            (activity as HomeActivity?)?.popFragment()
            // Dismiss the bottom sheet
            dismiss()
        }


    }

    fun setLocale(context: Context, languageCode: String) {
        setLocale(context, languageCode)
    }

    fun changeLanguage(activity: Activity, newLanguage: String) {
        PreferenceManager.getInstance(activity).saveLanguage(newLanguage)
        applyLanguage(newLanguage)
        restartApp(activity)
    }

    fun applyLanguage(langTag: String) {
        // Example langTag: "en", "en-US", "ur", "ar"
        val locales = LocaleListCompat.forLanguageTags(langTag)
        AppCompatDelegate.setApplicationLocales(locales) // persists automatically
    }

    fun restartApp(activity: Activity) {
        val intent = Intent(activity, HomeActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
        }
        activity.startActivity(intent)
        activity.finish()
    }
}