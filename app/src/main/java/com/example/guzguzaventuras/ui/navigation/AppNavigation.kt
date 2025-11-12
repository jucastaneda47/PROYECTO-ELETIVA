package com.example.guzguzaventuras.ui.navigation

import Level12Screen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.guzguzaventuras.ui.Inicio.InicioScreen
import com.example.guzguzaventuras.ui.tio.TioScreen
import com.example.guzguzaventuras.ui.bar.BarScreen
import com.example.guzguzaventuras.ui.nightclub.NightclubScreen
import com.example.guzguzaventuras.ui.levels.*
import com.example.guzguzaventuras.ui.levels.bar.*
import com.example.guzguzaventuras.ui.levels.club.*
import com.example.guzguzaventuras.ui.levels.tio.*
import com.example.guzguzaventuras.ui.levels_wasa.Levels2Screen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "inicio" // 🏠 Pantalla principal
    ) {

        // 🏠 PANTALLA PRINCIPAL
        composable("inicio") { InicioScreen(navController) }

        // 🏡 MUNDO 1 — CASA DEL TÍO
        composable("tio") { TioScreen(navController) }
        composable("levels") { LevelsScreen(navController) }

        // 🌴 MUNDO 2 — WASA WASA
        composable("bar") { BarScreen(navController) }
        composable("levels2") { Levels2Screen(navController) }

        // 🌃 MUNDO 3 — NIGHTCLUB
        composable("nightclub") { NightclubScreen(navController) }
        composable("levels3") { Levels3Screen(navController) } // ✅ Menú de niveles del tercer mundo

        // 🎮 NIVELES DEL MUNDO 1 (CASA DEL TÍO)
        composable("level1") { Level1Screen(navController) }
        composable("level2") { Level2Screen(navController) }
        composable("level3") { Level3Screen(navController) }
        composable("level4") { Level4Screen(navController) }

        // 🎮 NIVELES DEL MUNDO 2 (WASA WASA)
        composable("level5") { Level5Screen(navController) }
        composable("level6") { Level6Screen(navController) }
        composable("level7") { Level7Screen(navController) }
        composable("level8") { Level8Screen(navController) }

        // 🎮 NIVELES DEL MUNDO 3 (NIGHTCLUB)
        composable("level9") { Level9Screen(navController) }
        composable("level10") { Level10Screen(navController) }
        composable("level11") { Level11Screen(navController) }
        composable("level12") { Level12Screen(navController) }
    }
}