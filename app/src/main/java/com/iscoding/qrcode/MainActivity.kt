package com.iscoding.qrcode

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.iscoding.qrcode.features.scan.storage.domain.StorageImageAnalyzer
import com.iscoding.qrcode.graph.RootNavigationGraph
import com.iscoding.qrcode.graph.Screens
import com.iscoding.qrcode.ui.theme.QRCodeTheme
import com.iscoding.qrcode.util.LocaleHelper

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.updateLocale(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val context = this
            val navController = rememberNavController()
            QRCodeTheme {
                RootNavigationGraph(navController = navController)
            }
            if (Intent.ACTION_SEND == intent.action && intent.type?.startsWith("image/") == true) {
                handleSharedImage(intent, navController = navController)
            }
        }
    }

    private fun handleSharedImage(
        intent: Intent,
        navController: NavHostController,
    ) {
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
                val analyzer =
                    StorageImageAnalyzer(
                        onNoQRCodeFound = {
                            Toast.makeText(this, "This is not QR Code Image ", Toast.LENGTH_LONG).show()
                        },
                    ) { qrCodeData ->
                        // Navigate using deep link
                        val deepLinkUri =
                            Uri.parse(
                                "qrcodebuddy://${Screens.ShowQRCodeDataScreenDeepLink}/$qrCodeData/${
                                    Uri.encode(
                                        imageUri.toString(),
                                    )
                                }",
                            )
                        navController.navigate(deepLinkUri)
                    }
                analyzer.analyze(imageUri, it)
            }
        }
    }
}
