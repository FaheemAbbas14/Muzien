package com.tt.muzien


/**
 * Created by Faheem Abbas on 12/03/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
import android.app.Application
import android.content.Context
import com.tt.muzien.utilities.LocaleHelper
import com.tt.muzien.utilities.PreferenceManager

class Application : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.setLocale(base, PreferenceManager.getInstance(base).getLanguage()))
    }
}