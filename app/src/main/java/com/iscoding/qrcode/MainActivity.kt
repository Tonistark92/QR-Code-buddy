package com.iscoding.qrcode

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.iscoding.qrcode.graph.RootNavigationGraph
import com.iscoding.qrcode.graph.Screens
import com.iscoding.qrcode.features.scancode.fromstorage.domain.StorageImageAnalyzer
import com.iscoding.qrcode.ui.theme.QRCodeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = this
            val permisions = arrayOf(

                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )

            LaunchedEffect(key1 = true) {
                ActivityCompat.requestPermissions(context , permisions, 11)

            }
            val navController = rememberNavController()
            QRCodeTheme {
                RootNavigationGraph(navController = navController)
            }
            if (Intent.ACTION_SEND == intent.action && intent.type?.startsWith("image/") == true) {
                handleSharedImage(intent, navController = navController)
            }
        }

    }
    private fun handleSharedImage(intent: Intent, navController: NavHostController) {
        val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        }
        if (imageUri != null) {
            val inputStream = contentResolver.openInputStream(imageUri)
            inputStream?.use {
                val analyzer = StorageImageAnalyzer(
                    onNoQRCodeFound = {
                    Toast.makeText(this, "This is not QR Code Image ", Toast.LENGTH_LONG).show()

                }) { qrCodeData ->
                    // Navigate using deep link
                    val deepLinkUri = Uri.parse("qrcodebuddy://${Screens.ShowQRCodeDataScreenDeepLink}/$qrCodeData/${
                        Uri.encode(
                            imageUri.toString()
                        )
                    }")
                    navController.navigate(deepLinkUri)
                }
                analyzer.analyze(imageUri, it)
            }
        }
    }
}
