package com.iscoding.qrcode.features.mainactivity.intent

import android.content.Intent
import android.net.Uri
import java.io.InputStream

/**
 * Represents all user or system events that [MainActivity] can handle.
 *
 * Events are inputs into the MVI flow that describe *what happened*,
 * not *what should be done* (effects handle that).
 */
sealed class MainActivityEvent {

    /**
     * Event triggered when the user selects an image to analyze for QR codes.
     *
     * @param uri The URI of the selected image (from gallery, storage, etc.).
     * @param inputStream The input stream of the image, used for analysis.
     */
    data class OnAnalyzeImage(
        val uri: Uri,
        val inputStream: InputStream,
        val intent: Intent,
    ) : MainActivityEvent()

    /**
     * Event triggered when a new intent is delivered to [MainActivity].
     * Typically used for handling deep links, share intents, etc.
     */
    data class OnNewIntentReceived(val intent: Intent) : MainActivityEvent()
}
