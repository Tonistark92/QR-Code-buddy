package com.iscoding.qrcode.graph

/**
 * Defines all navigation routes used in the QR Code app.
 *
 * This object provides a centralized place to store route constants
 * for Jetpack Compose Navigation or any navigation framework.
 *
 * ### Why?
 * - Keeps route definitions consistent and avoids typos.
 * - Makes navigation changes easier by having a single source of truth.
 *
 * ### Example
 * ```kotlin
 * // Navigate to Scan screen
 * navController.navigate(Screens.ScanCode)
 * ```
 */
class Screens {
    companion object Routes {

        /** Route for the main/home screen. */
        const val MAIN_SCREEN = "/"

        /** Route for the QR code scanning screen. */
        const val SCAN_CODE = "/scan"

        /** Route for the QR code generation screen. */
        const val GENERATE_CODE = "/generate"

        /** Route for showing all images (from storage). */
        const val SHOW_ALL_IMAGES_SCREEN = "/StorageScanScreen"

        /** Deep link identifier for showing QR code data details. */
        const val SHOW_QR_CODE_DATA_SCREEN_DEEP_LINK = "showqrcodedatascreen"

        /** Route for displaying QR code details. */
        const val SHOW_QR_CODE_DATA_SCREEN = "/ShowQrCodeDataScreen"

        /** Route for the "choose between Camera or Storage" screen. */
        const val ASK_FROM_CAMERA_OR_STORAGE_SCREEN = "/AskFromCameraOrStorageScreen"
    }
}
