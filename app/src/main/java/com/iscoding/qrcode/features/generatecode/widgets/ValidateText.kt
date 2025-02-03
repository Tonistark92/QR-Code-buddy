package com.iscoding.qrcode.features.generatecode.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun ValidatedTextField(
    label: String,
    value: String,
    errorMessage: String,
    shouldShowError: Boolean,
    onValueChange: (String) -> Unit
) {
    Column {
        TextField(
            value = value,
            onValueChange = onValueChange,
            shape = RectangleShape,

            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors (
                focusedContainerColor = MaterialTheme.colorScheme.tertiary, // Only customize this
                unfocusedContainerColor = MaterialTheme.colorScheme.surface, // Optional
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,  // Optional
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant ,// Optional
                cursorColor =  MaterialTheme.colorScheme.primary,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Gray,
                errorLabelColor = Color.Red,

                )
        )
        Spacer(modifier = Modifier.height(6.dp))
        if (shouldShowError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}