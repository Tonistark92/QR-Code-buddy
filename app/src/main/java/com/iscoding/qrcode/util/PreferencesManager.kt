package com.iscoding.qrcode.util

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {

    private const val PREF_NAME = "app_preferences"
    private const val HAS_PERMISSIONS = "has_permissions"
    private const val IS_FIRST_LAUNCH = "is_first_launch"

    private lateinit var sharedPreferences: SharedPreferences

    // Initialize the SharedPreferences
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Save whether the app has permissions
    fun setHasPermissions(hasPermissions: Boolean) {
        sharedPreferences.edit().putBoolean(HAS_PERMISSIONS, hasPermissions).apply()
    }

    // Retrieve the permissions status
    fun hasPermissions(): Boolean {
        return sharedPreferences.getBoolean(HAS_PERMISSIONS, false)
    }

    // Save whether this is the first launch
    fun setIsFirstLaunch(isFirstLaunch: Boolean) {
        sharedPreferences.edit().putBoolean(IS_FIRST_LAUNCH, isFirstLaunch).apply()
    }

    // Check if this is the first launch
    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true)
    }
}
