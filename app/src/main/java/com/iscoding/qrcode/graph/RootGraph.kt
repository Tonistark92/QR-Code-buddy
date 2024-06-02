package com.iscoding.qrcode.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.iscoding.qrcode.generatecode.GenerateQRCode
import com.iscoding.qrcode.generatecode.MainScreen
import com.iscoding.qrcode.scancode.ScanCodeScreen

@Composable
fun RootNavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screens.MainScreen
    ) {
        composable(route = Screens.MainScreen) {
           MainScreen(navController = navController)

        }
        composable(route = Screens.ScanCode) {
            ScanCodeScreen()

        }
        composable(route = Screens.GenerateCode) {
            GenerateQRCode()
        }
    }
}