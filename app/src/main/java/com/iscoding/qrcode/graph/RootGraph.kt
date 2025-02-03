package com.iscoding.qrcode.graph

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.iscoding.qrcode.features.generatecode.GenerateQRCodeScreen
import com.iscoding.qrcode.features.generatecode.MainScreen
import com.iscoding.qrcode.features.scancode.AskFromCameraOrStorageScreen
import com.iscoding.qrcode.features.scancode.fromcamera.ScanCodeScreen
import com.iscoding.qrcode.features.scancode.fromstorage.presentation.ShowAllImagesScreen
import com.iscoding.qrcode.features.scancode.fromstorage.presentation.ShowQRCodeImageData

@SuppressLint("NewApi")
@Composable
fun RootNavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screens.MainScreen
    ) {
        composable(route = Screens.MainScreen) {
            AnimatedScreenTransition {

                MainScreen(navController = navController)
            }

        }
        composable(route = Screens.ScanCode) {
            AnimatedScreenTransition {

                ScanCodeScreen()
            }
        }
        composable(route = Screens.GenerateCode) {
            AnimatedScreenTransition {

                GenerateQRCodeScreen()
            }
        }
        composable(route = Screens.ShowAllImagesScreen) {
            AnimatedScreenTransition {


            ShowAllImagesScreen(navController = navController)
            }
        }
        composable(route = Screens.GenerateCode) {
            AnimatedScreenTransition {

                GenerateQRCodeScreen()
            }
            }
        composable(route = Screens.AskFromCameraOrStorageScreen) {
            AnimatedScreenTransition {

                AskFromCameraOrStorageScreen(navController = navController)
            }
            }

        composable(
            //      /ShowQrCodeDataScreen
            route = "${Screens.ShowQRCodeDataScreen}/{qrCodeData}/{imageUri}",
            arguments = listOf(
                navArgument("qrCodeData") { type = NavType.StringType },
                navArgument("imageUri") { type = NavType.StringType }
            ),
            deepLinks = listOf(navDeepLink {
                uriPattern =
                    "qrcodebuddy://${Screens.ShowQRCodeDataScreenDeepLink}/{qrCodeData}/{imageUri}"
            })
        ) { backStackEntry ->
            val qrCodeData = backStackEntry.arguments?.getString("qrCodeData")
            val imageUri = backStackEntry.arguments?.getString("imageUri")
            AnimatedScreenTransition{

            ShowQRCodeImageData(qrCodeData = qrCodeData!!, imageUri = imageUri!!)
            }


        }
    }
}

@Composable
fun AnimatedScreenTransition(
//    isVisible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(500)) + slideInHorizontally(initialOffsetX = { it }),
        exit = fadeOut(animationSpec = tween(500)) + slideOutHorizontally(targetOffsetX = { -it })
    ) {
        content()
    }
}