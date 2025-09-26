package com.iscoding.qrcode.features.generate.event

import com.iscoding.qrcode.features.generate.util.QrDataType

/**
 * Represents all possible user actions (events) in the "Generate QR Code" feature.
 *
 * These events are typically dispatched from the UI layer to the ViewModel,
 * which then processes them and updates the state accordingly.
 */
sealed class GenerateQrEvent {

    // ─────────────────────────────
    // Input Field Updates
    // ─────────────────────────────

    /** Triggered when plain text input is updated. */
    data class OnTextChanged(val text: String) : GenerateQrEvent()

    /** Triggered when URL input is updated. */
    data class OnUrlChanged(val url: String) : GenerateQrEvent()

    /** Triggered when email input is updated. */
    data class OnMailChanged(val email: String) : GenerateQrEvent()

    /** Triggered when telephone number input is updated. */
    data class OnTelChanged(val tel: String) : GenerateQrEvent()

    /** Triggered when SMS number input is updated. */
    data class OnSmsNumberChanged(val number: String) : GenerateQrEvent()

    /** Triggered when SMS message input is updated. */
    data class OnSmsMessageChanged(val message: String) : GenerateQrEvent()

    /** Triggered when geo latitude input is updated. */
    data class OnGeoLatitudeChanged(val latitude: String) : GenerateQrEvent()

    /** Triggered when geo longitude input is updated. */
    data class OnGeoLongitudeChanged(val longitude: String) : GenerateQrEvent()

    /** Triggered when event subject input is updated. */
    data class OnEventSubjectChanged(val subject: String) : GenerateQrEvent()

    /** Triggered when event start datetime input is updated. */
    data class OnEventDTStartChanged(val start: String) : GenerateQrEvent()

    /** Triggered when event end datetime input is updated. */
    data class OnEventDTEndChanged(val end: String) : GenerateQrEvent()

    /** Triggered when event location input is updated. */
    data class OnEventLocationChanged(val location: String) : GenerateQrEvent()

    // ─────────────────────────────
    // Type Selection
    // ─────────────────────────────

    /** Triggered when the user selects a QR code data type. */
    data class OnTypePicked(val type: QrDataType) : GenerateQrEvent()

    // ─────────────────────────────
    // Clear output
    // ─────────────────────────────

    /** Triggered when the QR code for a given type should be cleared. */
    data class ClearQRCode(val type: QrDataType) : GenerateQrEvent()

    // ─────────────────────────────
    // Final QR Code Generation
    // ─────────────────────────────

    /** Triggered when the user requests QR code generation. */
    object GenerateQRCode : GenerateQrEvent()

    // ─────────────────────────────
    // Share / Save
    // ─────────────────────────────

    /** Triggered when the user requests to share the generated QR code. */
    object ShareQRCode : GenerateQrEvent()
}
