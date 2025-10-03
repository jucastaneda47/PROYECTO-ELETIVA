package com.example.guzguzaventuras.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.guzguzaventuras.ui.Inicio.InicioScreen
import com.example.guzguzaventuras.ui.tio.TioScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "inicio") {
        composable("inicio") { InicioScreen(navController) }
        composable("tio") { TioScreen(navController) }
    }
}