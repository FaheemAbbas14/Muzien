package com.tt.muzien.utilities

import android.content.Context
import android.content.SharedPreferences


/**
 * Provides Data persistence and retrieval using Jetpack Preferences DataStore
 *
 * @property context Activity context
 */
class PreferenceManager private constructor(context: Context) {

    companion object {
        private const val PREF_NAME = "my_preferences"
        private var instance: PreferenceManager? = null

        @Synchronized
        fun getInstance(context: Context): PreferenceManager {
            if (instance == null) {
                instance = PreferenceManager(context.applicationContext)
            }
            return instance!!
        }
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun putString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return preferences.getString(key, defaultValue)
    }

    fun putInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return preferences.getInt(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }




}