package com.tt.muzien.utilities


/**
 * Created by Faheem Abbas on 21/05/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build

object FontScaleContextWrapper {
    fun wrap(context: Context): ContextWrapper {
        val config = Configuration(context.resources.configuration)
        config.fontScale = 1.0f // Fixed font scale (1.0 = normal)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val newContext = context.createConfigurationContext(config)
            ContextWrapper(newContext)
        } else {
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            ContextWrapper(context)
        }
    }
}
