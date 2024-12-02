package com.tt.muzien.ui.views


/**
 * Created by Faheem Abbas on 04/07/2024.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import com.tt.muzien.R

class CustomLoadingIndicator(context: Context, selectedColor: Int) :
    Dialog(context, R.style.TransparentDialog) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false) // Makes the dialog non-cancelable by default
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view: View = LayoutInflater.from(context).inflate(R.layout.custom_loader_layout, null)
        var loader: CustomCircularLoader = view.findViewById(R.id.loading_indicator)
//        loader.setColor(selectedColor)
        setContentView(view)
    }


}