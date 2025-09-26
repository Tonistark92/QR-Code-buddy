package com.iscoding.qrcode.util

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.LayoutDirection
import java.util.Locale

/**
 * Utility object that manages app-wide locale (language) configuration.
 *
 * This helper provides functions to:
 * - Save and retrieve the selected locale from SharedPreferences.
 * - Update the app context with the correct locale (supports both legacy and modern Android APIs).
 * - Expose [CompositionLocal]s for Jetpack Compose to access the current locale, layout direction,
 *   and configuration.
 *
 * It allows users to change the app language independently of the system language.
 */
object LocaleHelper {
    private const val PREFS_NAME = "locale_prefs"
    private const val KEY_LOCALE = "locale"

    /**
     * Updates the given [Context] with the currently selected locale.
     *
     * @param context The current app context.
     * @return A new [Context] with the applied locale.
     */
    fun updateLocale(context: Context): Context {
        val locale = getSelectedLocale(context)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResourcesLocale(context, locale)
        } else {
            updateResourcesLocaleLegacy(context, locale)
        }
    }

    /**
     * Persists the selected [locale] into SharedPreferences.
     *
     * @param context The current app context.
     * @param locale The [Locale] chosen by the user.
     */
    fun setLocale(
        context: Context,
        locale: Locale,
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LOCALE, locale.toLanguageTag()).apply()
    }

    /**
     * Retrieves the saved [Locale] from SharedPreferences.
     * If none is saved, falls back to the system default.
     *
     * @param context The current app context.
     * @return The stored [Locale], or system default if not found.
     */
    private fun getSelectedLocale(context: Context): Locale {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val localeTag = prefs.getString(KEY_LOCALE, Locale.getDefault().toLanguageTag())
        return Locale.forLanguageTag(localeTag!!)
    }

    /**
     * Updates the resources configuration with the given [locale] (Android N and above).
     *
     * @param context The current app context.
     * @param locale The [Locale] to apply.
     * @return A new [Context] with the updated locale configuration.
     */
    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResourcesLocale(
        context: Context,
        locale: Locale,
    ): Context {
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    /**
     * Updates the resources configuration with the given [locale] (legacy API < 24).
     *
     * @param context The current app context.
     * @param locale The [Locale] to apply.
     * @return The same [Context] with updated locale applied.
     */
    @Suppress("DEPRECATION")
    private fun updateResourcesLocaleLegacy(
        context: Context,
        locale: Locale,
    ): Context {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    /**
     * CompositionLocal for the current app [Locale].
     */
    val LocalAppLocale = compositionLocalOf { Locale.getDefault() }

    /**
     * CompositionLocal for the current [LayoutDirection] (LTR or RTL).
     */
    val LocalLayoutDirection = compositionLocalOf { LayoutDirection.Ltr }

    /**
     * CompositionLocal for the current [Configuration].
     * Throws an error if no config is provided.
     */
    val LocaleConfig = compositionLocalOf<Configuration> { error("No Config provided") }
}
