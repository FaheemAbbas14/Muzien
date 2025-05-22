package com.tt.muzien


/**
 * Created by Faheem Abbas on 12/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
import android.app.Application
import android.content.Context
import com.tt.muzien.utilities.FontScaleContextWrapper
import com.tt.muzien.utilities.LocaleHelper
import com.tt.muzien.utilities.PreferenceManager

class Application : Application() {
    override fun attachBaseContext(base: Context) {
        val languageUpdatedContext =
            LocaleHelper.setLocale(base, PreferenceManager.getInstance(base).getLanguage())
        val fontSafeContext = FontScaleContextWrapper.wrap(languageUpdatedContext)
        super.attachBaseContext(fontSafeContext)
    }

}