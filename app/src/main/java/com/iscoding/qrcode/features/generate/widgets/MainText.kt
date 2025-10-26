package com.iscoding.qrcode.features.generate.widgets

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

/**
 * A reusable text input field with error handling.
 *
 * This composable wraps a [TextField] inside a [Column], displays a label,
 * and optionally shows an error message below the field.
 *
 * It is designed to provide consistent styling and validation feedback
 * across your app.
 *
 * @param label The label text shown inside the text field.
 * @param value The current value of the text input.
 * @param errorMessage The error message to display when validation fails.
 * @param shouldShowError Whether the error message should be visible.
 * @param onValueChange Callback triggered whenever the user types in the field.
 */
@Composable
fun MainTextField(
    label: String,
    value: String,
    errorMessage: String,
    shouldShowError: Boolean,
    onValueChange: (String) -> Unit,
) {
    Column {
        TextField(
            value = value,
            onValueChange = onValueChange,
            shape = RectangleShape,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            colors =
            TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Gray,
                errorLabelColor = Color.Red,
            ),
        )
        Spacer(modifier = Modifier.height(6.dp))

        if (shouldShowError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}
