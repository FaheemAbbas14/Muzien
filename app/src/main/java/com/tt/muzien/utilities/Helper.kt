package com.tt.muzien.utilities

import android.content.Context


/**
 * Created by Faheem Abbas on 01/01/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
object Helper {
    fun dpToPx(context: Context,dpValue: Int): Int {
        return (dpValue * context.resources.displayMetrics.density).toInt()
    }
}