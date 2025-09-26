package com.iscoding.qrcode.features.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * A wrapper class for text that can be displayed in the UI.
 *
 * `UiText` allows you to handle both dynamic strings and string resources
 * in a consistent way. This is especially useful in MVVM or MVI patterns,
 * where the ViewModel should avoid direct dependency on Android resources.
 *
 * There are two types of `UiText`:
 * 1. [DynamicString] - Plain string values created at runtime.
 * 2. [StringResource] - References to Android string resources, with optional formatting arguments.
 *
 * Example usage:
 * ```
 * val dynamicText = UiText.DynamicString("Hello World")
 * val resourceText = UiText.StringResource(R.string.greeting, arrayOf(userName))
 * ```
 */
sealed class UiText {

    /**
     * Represents a dynamic string value created at runtime.
     *
     * @param value The actual string content.
     */
    data class DynamicString(val value: String) : UiText()

    /**
     * Represents a string resource from `res/values/strings.xml`.
     *
     * @param id The resource ID of the string.
     * @param args Optional formatting arguments for placeholders in the string resource.
     */
    class StringResource(
        @StringRes val id: Int,
        val args: Array<Any> = arrayOf(),
    ) : UiText()

    // -------------------------------
    // Conversion Functions
    // -------------------------------

    /**
     * Converts the `UiText` to a [String] using a Compose [LocalContext].
     *
     * Can be used directly in Compose UI:
     * ```
     * Text(text = myUiText.asString())
     * ```
     *
     * @return The resolved string.
     */
    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> LocalContext.current.getString(id, *args)
        }
    }

    /**
     * Converts the `UiText` to a [String] using a regular Android [Context].
     *
     * Useful in ViewModels, utility classes, or anywhere Compose context is unavailable:
     * ```
     * val text = myUiText.asString(context)
     * ```
     *
     * @param context The Android context used to resolve string resources.
     * @return The resolved string.
     */
    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(id, *args)
        }
    }
}
