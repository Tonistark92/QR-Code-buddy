package com.iscoding.qrcode

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.iscoding.qrcode.graph.RootNavigationGraph
import com.iscoding.qrcode.graph.Screens
import com.iscoding.qrcode.scancode.fromstorage.domain.StorageImageAnalyzer
import com.iscoding.qrcode.ui.theme.QRCodeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QRCodeTheme {
                RootNavigationGraph(navController = rememberNavController())
            }
        }
        if (Intent.ACTION_SEND == intent.action && intent.type?.startsWith("image/") == true) {
            handleSharedImage(intent)
        }
    }
    private fun handleSharedImage(intent: Intent) {
        val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        if (imageUri != null) {


            // Example: Pass imageUri to your QR code scanner/analyzer
            val analyzer = StorageImageAnalyzer { qrCode ->
                // Handle QR code data here
                Log.d("MainActivity", "Scanned QR code: $qrCode")
                // Navigate to appropriate screen or take action based on the QR code content
            }
            val inputStream = this.contentResolver.openInputStream(imageUri)
            inputStream?.use {
                StorageImageAnalyzer { qrCodeData ->
                    // Handle QR code data here, e.g., show a dialog, navigate to a new screen
                    Log.d("ShowAllImagesScreen", "Scanned QR Code: $qrCodeData")
                    // Example: Show a Toast
                    Toast.makeText(this, "QR Code Data: $qrCodeData", Toast.LENGTH_LONG).show()
//                    navController.navigate( "${Screens.ShowQRCodeDataScreen}/$qrCodeData/$uri")
                }.analyze(imageUri, it)
            }
        }
    }
}
