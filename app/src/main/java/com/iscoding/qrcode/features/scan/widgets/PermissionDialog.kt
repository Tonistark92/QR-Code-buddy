package com.iscoding.qrcode.features.scan.widgets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionDialog(
    title: String,
    body: String,
    onDismiss: ()-> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.wrapContentSize(),
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text= "Ok")
            }
        },
        title = {
            Text(text = title,
                fontWeight = FontWeight.SemiBold)
        },
        text = {
            Text(text = body,
                fontWeight = FontWeight.Medium
                )
        }
    )

}