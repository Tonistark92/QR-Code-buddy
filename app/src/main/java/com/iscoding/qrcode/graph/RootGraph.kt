package com.iscoding.qrcode.graph

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.iscoding.qrcode.features.generate.GenerateQRCodeScreen
import com.iscoding.qrcode.features.generate.MainScreen
import com.iscoding.qrcode.features.scan.AskFromCameraOrStorageScreen
import com.iscoding.qrcode.features.scan.camera.ScanCodeScreen
import com.iscoding.qrcode.features.scan.storage.allimages.StorageScanScreen
import com.iscoding.qrcode.features.scan.storage.details.QrDetailScreen

/**
 * Root navigation graph of the application.
 *
 * Defines all available destinations (screens) and their navigation routes
 * using Jetpack Compose Navigation.
 *
 * ### Features:
 * - Handles navigation between **Main**, **Scan**, **Generate**, **Storage** and **Detail** screens.
 * - Supports deep links for opening QR details directly (`qrcodebuddy://...`).
 * - Wraps each destination inside [AnimatedScreenTransition] for smooth
 * fade & slide transitions.
 *
 * @param navController The [NavHostController] that manages navigation state.
 */
@SuppressLint("NewApi")
@Composable
fun RootNavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screens.MAIN_SCREEN,
    ) {
        // Main screen
        composable(route = Screens.MAIN_SCREEN) {
            AnimatedScreenTransition {
                MainScreen(navController = navController)
            }
        }

        // Camera scan screen
        composable(route = Screens.SCAN_CODE) {
            AnimatedScreenTransition {
                ScanCodeScreen()
            }
        }

        // QR Code generation screen
        composable(route = Screens.GENERATE_CODE) {
            AnimatedScreenTransition {
                GenerateQRCodeScreen()
            }
        }

        // Show all storage images screen
        composable(route = Screens.SHOW_ALL_IMAGES_SCREEN) {
            AnimatedScreenTransition {
                StorageScanScreen(navController = navController)
            }
        }

        // Ask from Camera or Storage screen
        composable(route = Screens.ASK_FROM_CAMERA_OR_STORAGE_SCREEN) {
            AnimatedScreenTransition {
                AskFromCameraOrStorageScreen(navController = navController)
            }
        }

        // QR Code Details screen (with arguments + deep link)
        composable(
            route = "${Screens.SHOW_QR_CODE_DATA_SCREEN}/{qrCodeData}/{imageUri}",
            arguments = listOf(
                navArgument("qrCodeData") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("imageUri") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern =
                        "qrcodebuddy://${Screens.SHOW_QR_CODE_DATA_SCREEN_DEEP_LINK}/{qrCodeData}/{imageUri}"
                },
            ),
        ) { backStackEntry ->
            val encodedQrCodeData = backStackEntry.arguments?.getString("qrCodeData") ?: ""
            val encodedImageUri = backStackEntry.arguments?.getString("imageUri") ?: ""

            // Validate arguments
            if (encodedQrCodeData.isBlank() || encodedImageUri.isBlank()) {
                Text("Invalid QR code data")
                return@composable
            }

            // Decode args safely
            val qrCodeData = try {
                Uri.decode(encodedQrCodeData)
            } catch (e: Exception) {
                Log.e("Navigation", "Failed to decode QR data: $encodedQrCodeData")
                encodedQrCodeData
            }

            val imageUri = try {
                Uri.decode(encodedImageUri)
            } catch (e: Exception) {
                Log.e("Navigation", "Failed to decode image URI: $encodedImageUri")
                encodedImageUri
            }

            AnimatedScreenTransition {
                QrDetailScreen(qrCodeData = qrCodeData, imageUri = imageUri)
            }
        }
    }
}

/**
 * Wraps screen content with a fade & slide transition.
 *
 * Used in [RootNavigationGraph] to animate screen entry/exit.
 *
 * @param content The composable screen content.
 */
@Composable
fun AnimatedScreenTransition(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(500)) +
            slideInHorizontally(initialOffsetX = { it }),
        exit = fadeOut(animationSpec = tween(500)) +
            slideOutHorizontally(targetOffsetX = { -it }),
    ) {
        content()
    }
}
