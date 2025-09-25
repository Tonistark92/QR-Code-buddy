package com.iscoding.qrcode.features.scan.storage.details

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberImagePainter
import com.iscoding.qrcode.features.scan.storage.details.intent.QrDetailsEffect
import com.iscoding.qrcode.features.scan.storage.details.intent.QrDetailsEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

@Composable
fun QrDetailScreen(
    qrCodeData: String,
    imageUri: String,
) {
    val viewModel = koinViewModel<QrDetailsViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(true) {
        viewModel.onEvent(
            QrDetailsEvent.OnInitialDetailsScreen(
                qrCodeData = qrCodeData,
                imageUri = imageUri,
            ),
        )
    }
    LaunchedEffect(Unit) {
        viewModel.effect.distinctUntilChanged()
            .collectLatest { event ->
                when (event) {
                    is QrDetailsEffect.OnOpenUrl -> {
                        val intent = Intent(Intent.ACTION_VIEW, state.qrData.toUri())
                        context.startActivity(intent)
                    }

                    is QrDetailsEffect.OnQrDataLongPressed -> {
                        clipboardManager.setText(AnnotatedString(qrCodeData))
                    }

                    is QrDetailsEffect.ShowToast -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = Color(0xFF138173),
                trackColor = Color(0xFF8AE5D0),
            )
        } else {
            Column(
                modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
                    .padding(20.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "QR Code Data Scanned from the image selected:",
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = qrCodeData,
                    fontWeight = FontWeight.Bold,
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    viewModel.onEvent(QrDetailsEvent.OnQrDataLongPressed)
                                },
                            )
                        },
                    color = MaterialTheme.colorScheme.surface,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "If you long press on it will copy in your keyboard!",
                    color = MaterialTheme.colorScheme.surface,
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Display the image using the provided URI
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = rememberImagePainter(imageUri),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(300.dp),
                    )

                    // Display a button to open the URL in a browser if valid
                    if (state.isValidUrl) {
                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                viewModel.onEvent(QrDetailsEvent.OnOpenUrl)
                            },
                            shape = RectangleShape,
                            colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary, // Background color
                                contentColor = Color.White, // Text/Icon color
                                disabledContainerColor = Color.Gray, // Disabled background
                                disabledContentColor = Color.Black, // Disabled text color
                            ),
                            modifier = Modifier.padding(16.dp),
                        ) {
                            Text("Open in Browser")
                        }
                    }
                }
            }
        }
    }
}
