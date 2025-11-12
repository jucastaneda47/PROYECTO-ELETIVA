package com.example.guzguzaventuras.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.guzguzaventuras.ui.Inicio.InicioScreen
import com.example.guzguzaventuras.ui.tio.TioScreen
import com.example.guzguzaventuras.ui.bar.BarScreen
import com.example.guzguzaventuras.ui.nightclub.NightclubScreen
import com.example.guzguzaventuras.ui.levels.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        // 🏠 Pantallas principales
        composable("inicio") { InicioScreen(navController) }
        composable("tio") { TioScreen(navController) }
        composable("bar") { BarScreen(navController) }
        composable("nightclub") { NightclubScreen(navController) }
        composable("levels") { LevelsScreen(navController) }

        // 🎮 Niveles del juego
        composable("level1") { Level1Screen(navController) }
        composable("level2") { Level2Screen(navController) }
        composable("level3") { Level3Screen(navController) }
        composable("level4") { Level4Screen(navController) }
        composable("level5") { Level5Screen(navController) }
        composable("level6") { Level6Screen(navController) }
        composable("level7") { Level7Screen(navController) }
        composable("level8") { Level8Screen(navController) }
        composable("level9") { Level9Screen(navController) }

    }
}
