package com.example.guzguzaventuras.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.guzguzaventuras.ui.Inicio.InicioScreen
import com.example.guzguzaventuras.ui.tio.TioScreen
import com.example.guzguzaventuras.ui.bar.BarScreen
import com.example.guzguzaventuras.ui.nightclub.NightclubScreen
import com.example.guzguzaventuras.ui.levels.LevelsScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "inicio") {
        composable("inicio") { InicioScreen(navController) }
        composable("tio") { TioScreen(navController) }
        composable("bar") { BarScreen(navController) }
        composable("nightclub") { NightclubScreen(navController) }
        composable("levels") { LevelsScreen(navController)}
        }
}