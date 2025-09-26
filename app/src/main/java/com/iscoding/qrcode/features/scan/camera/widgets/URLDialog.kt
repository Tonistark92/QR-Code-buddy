package com.iscoding.qrcode.features.scan.camera.widgets

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * A dialog that displays a URL or text detected from a scanned QR code
 * and allows the user to take an action (e.g., open the URL).
 *
 * @param title The title of the dialog.
 * @param body The body text or message of the dialog.
 * @param confirmButtonText Text for the confirm button (e.g., "Open URL").
 * @param dismissButtonText Text for the dismiss button (e.g., "Cancel").
 * @param onConfirm Callback invoked when the confirm button is pressed.
 * @param onDismiss Callback invoked when the dismiss button is pressed or the dialog is dismissed.
 */
@Composable
fun URLDialog(
    title: String,
    body: String,
    confirmButtonText: String,
    dismissButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss, // Correctly pass the lambda
        title = { Text(text = title) },
        text = { Text(text = body) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissButtonText)
            }
        },
    )
}
