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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.iscoding.qrcode.graph.RootNavigationGraph
import com.iscoding.qrcode.graph.Screens
import com.iscoding.qrcode.scancode.fromstorage.domain.StorageImageAnalyzer
import com.iscoding.qrcode.ui.theme.QRCodeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
        val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        if (imageUri != null) {
            val inputStream = contentResolver.openInputStream(imageUri)
            inputStream?.use {
                val analyzer = StorageImageAnalyzer { qrCodeData ->
                    // Navigate using deep link
                    val deepLinkUri = Uri.parse("qrcode://showqrcodedatascreen/$qrCodeData/${
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
