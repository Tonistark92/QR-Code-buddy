package com.iscoding.qrcode.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Singleton object for managing application preferences using [SharedPreferences].
 *
 * This class handles saving and retrieving simple key-value pairs such as:
 * - Whether the app has necessary permissions.
 * - Whether it's the first launch of the app.
 *
 * ⚠️ Must be initialized by calling [init] before using any other methods.
 */
object PreferencesManager {
    private const val PREF_NAME = "app_preferences"
    private const val HAS_PERMISSIONS = "has_permissions"
    private const val IS_FIRST_LAUNCH = "is_first_launch"

    private lateinit var sharedPreferences: SharedPreferences

    /**
     * Initializes the [SharedPreferences] instance.
     * This should typically be called once in [android.app.Application.onCreate].
     *
     * @param context The application context used to initialize preferences.
     */
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Saves whether the app has been granted all required permissions.
     *
     * @param hasPermissions True if the app has permissions, false otherwise.
     */
    fun setHasPermissions(hasPermissions: Boolean) {
        sharedPreferences.edit().putBoolean(HAS_PERMISSIONS, hasPermissions).apply()
    }

    /**
     * Retrieves whether the app currently has all required permissions.
     *
     * @return True if the app has permissions, false otherwise.
     */
    fun hasPermissions(): Boolean {
        return sharedPreferences.getBoolean(HAS_PERMISSIONS, false)
    }

    /**
     * Saves whether this is the first launch of the application.
     *
     * @param isFirstLaunch True if this is the first launch, false otherwise.
     */
    fun setIsFirstLaunch(isFirstLaunch: Boolean) {
        sharedPreferences.edit().putBoolean(IS_FIRST_LAUNCH, isFirstLaunch).apply()
    }

    /**
     * Checks whether this is the first time the app is launched.
     *
     * @return True if this is the first launch, false otherwise.
     */
    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true)
    }
}
