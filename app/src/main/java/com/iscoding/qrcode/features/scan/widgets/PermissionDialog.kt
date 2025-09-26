package com.iscoding.qrcode.features.scan.widgets

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * A reusable Compose dialog for requesting permissions or showing permission-related messages.
 *
 * This dialog displays a title, a body message, and two buttons: a confirm button and a dismiss/cancel button.
 * It is typically used in QR code scanning or camera access flows where runtime permissions are required.
 *
 * @param title The title of the dialog.
 * @param body The main message of the dialog explaining why the permission is needed.
 * @param confirmButtonText Text for the confirm button (e.g., "Allow" or "Grant").
 * @param onConfirm Lambda executed when the user clicks the confirm button.
 * @param onDismiss Lambda executed when the dialog is dismissed or the cancel button is clicked.
 *
 * Example usage:
 * ```
 * var showDialog by remember { mutableStateOf(true) }
 *
 * if (showDialog) {
 *     PermissionDialog(
 *         title = "Camera Permission",
 *         body = "This app needs camera access to scan QR codes.",
 *         confirmButtonText = "Grant",
 *         onConfirm = { /* request permission */ showDialog = false },
 *         onDismiss = { showDialog = false }
 *     )
 * }
 * ```
 */
@Composable
fun PermissionDialog(
    title: String,
    body: String,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = body) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
