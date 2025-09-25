package com.iscoding.qrcode.features.mainactivity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.iscoding.qrcode.features.mainactivity.intent.MainActivityEffect
import com.iscoding.qrcode.features.mainactivity.intent.MainActivityEvent
import com.iscoding.qrcode.graph.RootNavigationGraph
import com.iscoding.qrcode.graph.Screens
import com.iscoding.qrcode.theme.QrCodeBuddyTheme
import com.iscoding.qrcode.util.LocaleHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import logcat.logcat
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.updateLocale(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val viewModel = koinViewModel<MainActivityViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()

            val navController = rememberNavController()

            LaunchedEffect(Unit) {
                navController.enableOnBackPressed(true)
            }

            Box(contentAlignment = Alignment.Center) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = Color(0xFF138173),
                        trackColor = Color(0xFF8AE5D0),
                    )
                }
                QrCodeBuddyTheme {
                    RootNavigationGraph(navController = navController)
                }
            }
            if (Intent.ACTION_SEND == intent.action && intent.type?.startsWith("image/") == true) {
                viewModel.onEvent(MainActivityEvent.OnNewIntentReceived)
            }
//            LaunchedEffect(Unit) {
//                // Check if we should navigate on app resume
//                val currentState = viewModel.state.first()
//                if (currentState.qrCode.isNotEmpty() && currentState.imageUri != null) {
//                    // Navigate to QR details if we have data
//                    val encodedQrCode = Uri.encode(currentState.qrCode)
//                    val encodedImageUri = Uri.encode(currentState.imageUri.toString())
//                    val deepLinkUri = "qrcodebuddy://${Screens.ShowQRCodeDataScreenDeepLink}/${encodedQrCode}/${encodedImageUri}".toUri()
//                    navController.navigate(deepLinkUri)
//                }
//            }

            LaunchedEffect(Unit) {
                viewModel.effect.distinctUntilChanged()
                    .collectLatest { effect ->
                        when (effect) {
                            is MainActivityEffect.ShowToast -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    "This is not QR Code Image",
                                    Toast.LENGTH_LONG,
                                ).show()
                            }

                            is MainActivityEffect.NavigateToQrDetailsScreen -> {
                                val encodedQrCode = Uri.encode(effect.qrCode) // Use effect data
                                val encodedImageUri = Uri.encode(effect.imageUri.toString())

                                val deepLinkUri = "qrcodebuddy://${Screens.ShowQRCodeDataScreenDeepLink}/$encodedQrCode/$encodedImageUri".toUri()
                                logcat("ISLAAAAAM") { deepLinkUri.toString() }
                                navController.navigate(deepLinkUri)
                            }

                            is MainActivityEffect.AnalyzeImage -> {
                                handleSharedImageIntent(intent, viewModel)
                            }

//                            is MainActivityUiState.Error -> {
//                                Toast.makeText(
//                                    this@MainActivity,
//                                    (uiState as MainActivityUiState.Error).message,
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            }
                        }
                    }
            }
        }
    }

    private fun handleSharedImageIntent(intent: Intent, viewModel: MainActivityViewModel) {
        val imageUri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            }

        if (imageUri != null) {
            val inputStream = contentResolver.openInputStream(imageUri)
            inputStream?.use {
                viewModel.onEvent(MainActivityEvent.OnAnalyzeImage(imageUri, it))
            }
        }
    }
}
